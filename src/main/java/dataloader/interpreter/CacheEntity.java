package dataloader.interpreter;

import com.fasterxml.jackson.databind.JsonNode;
import dataloader.urlschema.params.Entity;

import java.util.HashMap;
import java.util.Map;

public class CacheEntity {
    private Map<Entity, Map<String, JsonNode>> cacheMap = new HashMap<>();

    public JsonNode getEntity(Entity entity, String key) throws IllegalArgumentException {
        if (cacheMap.containsKey(entity)) {
            final Map<String, JsonNode> jsonMap = cacheMap.get(entity);
            if (jsonMap.containsKey(key)) {
                return jsonMap.get(key);
            } else {
                throw new IllegalArgumentException();
            }
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void setEntity(Entity entity, String key, JsonNode jsonEntity) {
        if (cacheMap.containsKey(entity)) {
            cacheMap.get(entity).put(key, jsonEntity);
        } else {
            Map<String, JsonNode> entityMap = new HashMap<>();
            entityMap.put(key, jsonEntity);
            cacheMap.put(entity, entityMap);
        }
    }
}
