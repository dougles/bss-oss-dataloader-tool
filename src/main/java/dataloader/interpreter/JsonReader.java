package dataloader.interpreter;

import com.fasterxml.jackson.databind.node.ArrayNode;
import util.JsonMapper;

import java.io.File;
import java.io.IOException;

public class JsonReader {
    public static ArrayNode getFromFile(String jsonFile) throws IOException {
        File file = new File(jsonFile);
        if (file.exists() && !file.isDirectory()) {
            return JsonMapper.getJsonNodeFromFile(file, ArrayNode.class);
        } else {
            throw new IOException("File json to process not found");
        }
    }
}
