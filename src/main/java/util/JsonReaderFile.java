package util;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;

public class JsonReaderFile {
    /**
     * Read json from file.
     *
     * @param fileName file contains json data.
     * @return JsonNode built
     * @throws IOException if the json file has bad format.
     */
    public static JsonNode readJson(String fileName) throws IOException {
        File jsonFile = new File(fileName);
        if (jsonFile.exists() && jsonFile.canRead()) {
            ObjectMapper mapper = new ObjectMapper();
            return mapper.readTree(jsonFile);
        } else {
            throw new IOException("File json to process not found or it can not to be read");
        }
    }
}
