package dataloader.urlschema;

import dataloader.urlschema.params.ActionRest;
import dataloader.urlschema.params.Entity;
import log.DataLogger;

import java.lang.reflect.Field;
import java.util.Map;

public class EntityHttpVars {
    private Map<Entity, EntityActionRest> entitiesMap;
    public Map<String, String> headers;

    public EntityHttpVars(Map<Entity, EntityActionRest> entitiesMap, Map<String, String> headers) {
        this.entitiesMap = entitiesMap;
        this.headers = headers;
    }

    public String getAction(Entity entity, ActionRest actionRest, Object... args) {
        String urlParsed = null;
        EntityActionRest entityActionRest = entitiesMap.get(entity);
        try {
            Field declaredField = entityActionRest.getClass().getDeclaredField(actionRest.getValue());
            urlParsed = (String) declaredField.get(entityActionRest);
            if (args.length > 0) {
                urlParsed = String.format(urlParsed, args);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            DataLogger.warn(EntityHttpVars.class, "Parsing url error");
        }
        return urlParsed;
    }
}
