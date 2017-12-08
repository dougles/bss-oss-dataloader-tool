package kafka;

import com.fasterxml.jackson.databind.JsonNode;
import com.google.inject.Inject;
import org.apache.avro.specific.SpecificRecordBase;
import org.apache.kafka.clients.producer.Producer;
import org.apache.kafka.clients.producer.ProducerRecord;

public class KafkaProducer<T extends SpecificRecordBase> {
    private Producer<String, byte[]> producer;
    private Class<T> clazz;
    private String topic;

    @Inject
    public KafkaProducer(Producer<String, byte[]> producer, Class<T> clazz, String topic) {
        this.producer = producer;
        this.clazz = clazz;
        this.topic = topic;
    }

    public void sedMessage(String jsonMessage) {
        final byte[] bytes = ConvertUtils.jsonMessageToBytes(jsonMessage, this.clazz);
        producer.send(new ProducerRecord(this.topic, bytes));
    }

    public void sedMessage(JsonNode jsonMessage) {
        final byte[] bytes = ConvertUtils.jsonMessageToBytes(jsonMessage, this.clazz);
        producer.send(new ProducerRecord(this.topic, bytes));
    }

    public void closeProducer() {
        producer.close();
    }
}
