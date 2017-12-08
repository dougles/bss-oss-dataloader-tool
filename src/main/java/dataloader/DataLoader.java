package dataloader;

import avro.PaymentApplication.PaymentApplication;
import avro.ProductOrderNotification.ProductOrderNotification;
import com.fasterxml.jackson.databind.node.ArrayNode;

import com.google.inject.Inject;
import configuration.Configuration;
import dataloader.interpreter.ConstansPaths;
import dataloader.interpreter.EntityInterpreter;
import dataloader.interpreter.JsonReader;
import dataloader.interpreter.KafkaNotifier;
import exception.DataLoaderException;
import log.DataLogger;

import java.io.IOException;
import java.util.List;

public class DataLoader {

    private final EntityInterpreter interpreter;
    private final KafkaNotifier<PaymentApplication> paymentKafkaNotifier;
    private final KafkaNotifier<ProductOrderNotification> productOrderKafkaNotifier;

    @Inject
    public DataLoader(EntityInterpreter interpreter, KafkaNotifier<PaymentApplication> paymentKafkaNotifier, KafkaNotifier<ProductOrderNotification> productOrderKafkaNotifier) {
        this.interpreter = interpreter;
        this.paymentKafkaNotifier = paymentKafkaNotifier;
        this.productOrderKafkaNotifier = productOrderKafkaNotifier;
    }

    public void execute() {
        DataLogger.info(this.getClass(), "Starting Process");
        this.entityProcess();
        this.kafkaProcess();
        DataLogger.info(this.getClass(), "Ended Process");
    }

    private void entityProcess() {
        final List<String> jsonResources = Configuration.current().jsonResources;
        for (String file : jsonResources) {
            try {
                final ArrayNode jsonFromFile = JsonReader.getFromFile(ConstansPaths.PATH_TO_ENTITIES + file);
                interpreter.process(jsonFromFile);
            } catch (DataLoaderException | IOException e) {
                e.printStackTrace();
                DataLogger.warn(DataLoader.class, e.getMessage());
            }
        }
    }

    private void kafkaProcess() {
        DataLogger.info(this.getClass(), "Starting Kafka messages");
        try {
            final String organizationFile = (String) Configuration.current().kafkaResources.get(ConstansPaths.KafkaKeyFiles.ORGANIZATION);
            if (organizationFile != null) {
                final ArrayNode organizationsJson = JsonReader.getFromFile(ConstansPaths.PATH_TO_KAFKA + organizationFile);
                DataLogger.info(this.getClass(), "Starting messages to Kafka for Organizations");
                paymentKafkaNotifier.process(organizationsJson);
            }
            final String productOrderFile = (String) Configuration.current().kafkaResources.get(ConstansPaths.KafkaKeyFiles.PRODUCT_ORDER);
            if (productOrderFile != null) {
                final ArrayNode productOrdersJson = JsonReader.getFromFile(ConstansPaths.PATH_TO_KAFKA + productOrderFile);
                DataLogger.info(this.getClass(), "Starting messages to Kafka for ProductOrders");
                productOrderKafkaNotifier.process(productOrdersJson);
            }
            DataLogger.info(this.getClass(), "Ending Kafka messages");
        } catch (IOException e) {
            e.printStackTrace();
            DataLogger.warn(DataLoader.class, e.getMessage());
        }
    }
}
