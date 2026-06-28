package dataloader.urlschema;

import dataloader.urlschema.params.ActionRest;
import dataloader.urlschema.params.Entity;

import log.DataLogger;
import util.StringUtils;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class HttpVarsBuilder {
    public static EntityHttpVars build(Map varsMap) {
        List<Map> rawHeaders = (List<Map>) varsMap.get("headers");
        Map<String, String> headers = getHeaders(rawHeaders);
        List<Map> rawApis = (List<Map>) varsMap.get("api-rests");
        Map<Entity, EntityActionRest> entitiesMap = new HashMap<>();
        for (Map m : rawApis) {
            String baseUrl = (String) m.get("base-url");
            int port = (int) m.get("port");
            String mainUrl = String.format("http://%s:%d", baseUrl, port);
            List<Map> rawEntities = (List<Map>) m.get("entities");
            for (Map et : rawEntities) {
                String rawEntity = (String) et.get("entity");
                Map actionsMap = (Map) et.get("actions");
                EntityActionRest entityAction = new EntityActionRest();

                for (ActionRest action : ActionRest.values()) {
                    if (actionsMap.containsKey(action.getValue())) {
                        String rawAction = (String) actionsMap.get(action.getValue());
                        String urlFormatted = mainUrl + StringUtils.formatStringBrackets(rawAction, "%s");
                        try {
                            Field declaredField = entityAction.getClass().getDeclaredField(action.getValue());
                            declaredField.set(entityAction, urlFormatted);
                        } catch (NoSuchFieldException | IllegalAccessException e) {
                            e.printStackTrace();
                            DataLogger.warn(HttpVarsBuilder.class, e.getMessage());
                        }
                    }
                }

                Entity entity = Entity.valueOf(StringUtils.toUnderscore(rawEntity));
                entitiesMap.put(entity, entityAction);
            }
        }

        return new EntityHttpVars(entitiesMap, headers);
    }

    public static Map<String, String> getHeaders(List<Map> rawHeaders) {
        return rawHeaders.stream()
                .collect(Collectors.toMap(m -> (String) m.get("key"), m -> (String) m.get("value")));
    }
}
