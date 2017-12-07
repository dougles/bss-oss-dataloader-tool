package configuration;

import log.DataLogger;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

public class Configuration {
    private final static Yaml yaml = new Yaml();
    public Map httpVarsMap;
    public List<String> modules;
    public List<String> jsonResources;
    public Map kafkaResources;
    public String kafkaServer;

    private static Configuration configuration;

    private Configuration() {
    }

    public static Configuration current() {

        if (configuration == null) {
            configuration = new Configuration();
            Map conf = getConfiguration();

            if (conf.containsKey("modules")) {
                configuration.modules = (List<String>) conf.get("modules");
            } else {
                DataLogger.error(Configuration.class, "Modules Not found on configuration");
            }

            if (conf.containsKey("api-resources")) {
                configuration.jsonResources = (List<String>) conf.get("api-resources");
            } else {
                DataLogger.error(Configuration.class, "APIs configuration not found");
            }

            if (conf.containsKey("kafka-resources")) {
                configuration.kafkaResources = (Map) conf.get("kafka-resources");
            } else {
                DataLogger.error(Configuration.class, "Kafka configuration not found");
            }

            if (conf.containsKey("kafka-producer")) {
                configuration.kafkaServer = (String) conf.get("kafka-producer");
            } else {
                DataLogger.error(Configuration.class, "Kafka configuration not found");
            }

            configuration.httpVarsMap = getHttpVarsMap();
        }

        return configuration;
    }

    private static Map getHttpVarsMap() {
        final InputStream resourceAsStream = Configuration.class.getClassLoader().getResourceAsStream("api-configurations.yml");
        return (Map) yaml.load(resourceAsStream);
    }

    private static Map getConfiguration() {
        final InputStream resourceAsStream = Configuration.class.getClassLoader().getResourceAsStream("configurations.yml");
        return (Map) yaml.load(resourceAsStream);
    }
}
