package util;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.JsonSerializable;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public class JsonMapper {
    private final static ObjectMapper mapper = new ObjectMapper();

    public static <T extends JsonSerializable.Base> T getJsonNodeFromFile(File fileJson, Class<T> clazz) throws IOException {
        return mapper.readValue(fileJson, clazz);
    }

    public static <T extends JsonSerializable.Base> T getJsonNodeFromString(String stringJson, Class<T> clazz) throws IOException {
        return mapper.readValue(stringJson, clazz);
    }

    public static <T> T getObjectFromJsonString(String json, Class<T> clazz) throws IOException {
        return mapper.readValue(json, clazz);
    }

    public static <T> T getObjectFromJson(JsonNode json, Class<T> clazz) throws IOException {
        return mapper.treeToValue(json, clazz);
    }

    public static JsonNode getJson(InputStream inputStream) throws IOException {
        return mapper.readTree(inputStream);
    }

    public static ObjectNode createObjectJson(){
     return  mapper.createObjectNode();
    }

    public static String getStringValue(JsonNode json) {
        String value = null;
        try {
            value = mapper.writeValueAsString(json);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return value;
    }
}
