package kafka;

import configuration.Configuration;
import exception.DataLoaderException;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.Producer;

import java.util.Properties;

public class KafkaFactory {

    public static Producer<String, byte[]> createInstance() {
        Properties properties = new Properties();
        properties.put("bootstrap.servers", Configuration.current().kafkaServer);
        properties.put("acks", "all");
        properties.put("retries", 0);
        properties.put("batch.size", 16384);
        properties.put("linger.ms", 1);
        properties.put("buffer.memory", 33554432);
        properties.put("key.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");
        properties.put("value.serializer", "org.apache.kafka.common.serialization.ByteArraySerializer");

        return new KafkaProducer<>(properties);
    }
}
