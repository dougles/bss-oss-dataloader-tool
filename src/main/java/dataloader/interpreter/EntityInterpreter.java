package dataloader.interpreter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.fasterxml.jackson.databind.node.TextNode;
import com.google.inject.Inject;
import dataloader.http.BuildRequest;
import dataloader.http.JsonResponse;
import dataloader.urlschema.EntityHttpVars;
import dataloader.urlschema.params.ActionRest;
import dataloader.urlschema.params.Entity;
import exception.DataLoaderException;
import log.DataLogger;
import org.apache.http.HttpStatus;
import util.JsonMapper;
import util.StringUtils;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EntityInterpreter implements JsonInterpreter {

    private EntityHttpVars entityHttpVars;
    private CacheEntity cacheEntity;

    @Inject
    public EntityInterpreter(EntityHttpVars entityHttpVars, CacheEntity cacheEntity) {
        this.entityHttpVars = entityHttpVars;
        this.cacheEntity = cacheEntity;
    }

    @Override
    public void process(ArrayNode jsonNodes) throws DataLoaderException {
        int n = jsonNodes.size();
        for (int i = 0; i < n; i++) {
            JsonNode objectNode = jsonNodes.get(i);
            if (objectNode.has(InterpreterKeys.ENTITY) && objectNode.has(InterpreterKeys.DATA) && objectNode.has(InterpreterKeys.ACTION)) {
                String entityFormatted = StringUtils.toUnderscore(objectNode.get(InterpreterKeys.ENTITY).asText());
                String actionFormatted = StringUtils.toUnderscore(objectNode.get(InterpreterKeys.ACTION).asText());
                String cacheId = null;
                if (objectNode.has(InterpreterKeys.CACHE_ID)) {
                    cacheId = objectNode.get(InterpreterKeys.CACHE_ID).asText();
                }
                Entity entity = Entity.valueOf(entityFormatted);
                ActionRest action = ActionRest.valueOf(actionFormatted);
                switch (action) {
                    case CREATE:
                        String url = entityHttpVars.getAction(entity, action);
                        int times = objectNode.has(InterpreterKeys.MULTIPLE) ? objectNode.get(InterpreterKeys.MULTIPLE).intValue() : 1;
                        this.create(url, objectNode.path(InterpreterKeys.DATA), cacheId, entity, times);
                        break;
                    case UPDATE:
                        this.update(objectNode.path(InterpreterKeys.DATA), cacheId, entity);
                        break;
                    case DELETE:
                        this.delete(objectNode.path(InterpreterKeys.DATA), entity);
                        break;

                }
            }
        }
    }

    private void create(String url, JsonNode jsonData, String cacheId, Entity entity, int times) {
        Optional<String> idOptional = Optional.ofNullable(cacheId);
        for (int i = 0; i < times; i++) {
            try {
                JsonNode data = JsonMapper.getJsonNodeFromString(JsonMapper.getStringValue(jsonData), JsonNode.class);
                JsonNode jsonProcessed = this.makeEntity(data);
                final CompletableFuture<JsonResponse> jsonResponse = BuildRequest.createRequest(url)
                        .setHeaders(entityHttpVars.headers)
                        .setBody(jsonProcessed)
                        .post();
                jsonResponse.thenAccept(r -> {
                    if (r.status == HttpStatus.SC_CREATED) {
                        idOptional.ifPresent(s -> this.cacheEntity.setEntity(entity, s, r.jsonBody));
                        DataLogger.info("Entity created:" + JsonMapper.getStringValue(data));
                    } else {
                        DataLogger.error(this.getClass(), "It was not possible to create entity:" + JsonMapper.getStringValue(data));
                    }
                });
            } catch (IOException e) {
                e.getStackTrace();
                DataLogger.error(this.getClass(), "Error creating Entity:" + JsonMapper.getStringValue(jsonData));
            }
        }
    }

    private void update(JsonNode jsonData, String cacheId, Entity entity) {
        final String id = jsonData.get("id").asText();
        String url = entityHttpVars.getAction(entity, ActionRest.UPDATE, id);
        Optional<String> idOptional = Optional.ofNullable(cacheId);
        try {
            JsonNode jsonProcessed = this.makeEntity(jsonData);
            final CompletableFuture<JsonResponse> jsonResponse = BuildRequest.createRequest(url)
                    .setHeaders(entityHttpVars.headers)
                    .setBody(jsonProcessed)
                    .patch();
            jsonResponse.thenAccept(r -> {
                if (r.status == HttpStatus.SC_CREATED) {
                    idOptional.ifPresent(s -> this.cacheEntity.setEntity(entity, s, r.jsonBody));
                } else {
                    DataLogger.error(this.getClass(), "It was not possible to create entity:" + JsonMapper.getStringValue(jsonData));
                }
            });
        } catch (IOException e) {
            e.getStackTrace();
            DataLogger.error(this.getClass(), "Error updating Entity" + JsonMapper.getStringValue(jsonData));
        }
    }

    private void delete(JsonNode jsonData, Entity entity) {
        final String id = jsonData.get("id").asText();
        String url = entityHttpVars.getAction(entity, ActionRest.DELETE, id);
        final CompletableFuture<JsonResponse> jsonResponse = BuildRequest.createRequest(url)
                .setHeaders(entityHttpVars.headers)
                .delete();
        jsonResponse.thenAccept(r -> {
            if (r.status != HttpStatus.SC_OK) {
                DataLogger.error(this.getClass(), "Unable to update entity:" + entity);
            }
        });
    }

    private JsonNode makeEntity(JsonNode jsonEntity) throws IOException {
        ObjectNode objEntity = (ObjectNode) jsonEntity;
        Iterator<Map.Entry<String, JsonNode>> nodeIterator = objEntity.fields();
        Map<String, JsonNode> builtChildren = new HashMap<>();
        // process if the entity for create has sub entities for building.
        while (nodeIterator.hasNext()) {
            Map.Entry<String, JsonNode> jsonNodeEntry = nodeIterator.next();
            if (jsonNodeEntry.getKey().matches("@.*")) {
                if (jsonNodeEntry.getValue().getNodeType() == JsonNodeType.OBJECT) {
                    builtChildren.put(jsonNodeEntry.getKey(), this.makeSubEntityJson(jsonNodeEntry.getValue()));
                }
            }
            if (jsonNodeEntry.getValue().getNodeType() == JsonNodeType.STRING) {
                String propertyModified = StringUtils.formatStringBrackets(jsonNodeEntry.getValue().asText(), String.valueOf(System.currentTimeMillis()));
                TextNode t = new TextNode(propertyModified);
                jsonNodeEntry.setValue(t);
            }
        }
        //rebuild entity
        for (Map.Entry<String, JsonNode> jsonNodeEntry : builtChildren.entrySet()) {
            objEntity.set(jsonNodeEntry.getKey().substring(1), jsonNodeEntry.getValue());
            objEntity.remove(jsonNodeEntry.getKey());
        }

        return objEntity;
    }

    private JsonNode makeSubEntityJson(JsonNode jsonNode) throws IOException {
        JsonNode resultJson = this.getSubEntityJson(jsonNode);
        Iterator<Map.Entry<String, JsonNode>> nodeIterator = jsonNode.get(InterpreterKeys.MAP).fields();
        ObjectNode newJson = JsonMapper.createObjectJson();
        while (nodeIterator.hasNext()) {
            Map.Entry<String, JsonNode> jsonNodeEntry = nodeIterator.next();
            String property = jsonNodeEntry.getKey();
            if (resultJson.has(property)) {
                newJson.set(property, resultJson.get(property));
            }
        }

        return newJson;
    }

    private JsonNode getSubEntityJson(JsonNode jsonNode) throws IOException {
        JsonNode result = null;
        if (jsonNode.has(InterpreterKeys.FROM)) {
            String nestedAction = jsonNode.get(InterpreterKeys.FROM).textValue();
            String[] splitted = nestedAction.split("\\.");
            String nameAction = splitted[1];
            Entity entity = Entity.valueOf(StringUtils.toUnderscore(splitted[0]));
            Matcher matcher = Pattern.compile("'(.*?)'").matcher(nameAction);
            String field = "";
            if (matcher.find()) {
                field = matcher.group(1);
            }
            if (nameAction.contains(InterpreterKeys.GET_BY)) {
                String url = entityHttpVars.getAction(entity, ActionRest.GET, field);
                result = this.getFromEndPoint(url);
            } else if (nameAction.contains(InterpreterKeys.GET_FROM_CACHE)) {
                result = this.cacheEntity.getEntity(entity, field);
            }
            //TODO from file
        }
        if (result == null) {
            throw new IOException("Sub Entity does not exists: " + JsonMapper.getStringValue(jsonNode));
        }

        return result;
    }

    private JsonNode getFromEndPoint(String url) {
        JsonNode result = null;
        try {
            CompletableFuture<JsonResponse> jsonResponseCompletableFuture = BuildRequest.createRequest(url)
                    .setHeaders(entityHttpVars.headers)
                    .get();
            final JsonResponse jsonResponse = jsonResponseCompletableFuture.get();
            if (jsonResponse.status == HttpStatus.SC_OK) {
                result = jsonResponse.jsonBody;
            }
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        return result;
    }
}
