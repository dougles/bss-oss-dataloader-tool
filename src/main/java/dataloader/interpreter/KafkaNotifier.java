package dataloader.interpreter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.inject.Inject;
import kafka.KafkaProducer;

import org.apache.avro.specific.SpecificRecordBase;

public class KafkaNotifier<T extends SpecificRecordBase> implements JsonInterpreter {
    private KafkaProducer<T> kafkaProducer;

    @Inject
    public KafkaNotifier(KafkaProducer<T> kafkaProducer) {
        this.kafkaProducer = kafkaProducer;
    }

    @Override
    public void process(ArrayNode jsonNodes) {
        int n = jsonNodes.size();
        for (int i = 0; i < n; i++) {
            JsonNode objectNode = jsonNodes.get(i);
            kafkaProducer.sedMessage(objectNode);
        }
        kafkaProducer.closeProducer();
    }
}
