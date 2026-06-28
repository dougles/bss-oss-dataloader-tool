package module;

import avro.PaymentApplication.PaymentApplication;
import avro.ProductOrderNotification.ProductOrderNotification;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import dataloader.interpreter.KafkaNotifier;
import kafka.KafkaFactory;
import kafka.KafkaProducer;
import models.ConstantsModel;
import org.apache.kafka.clients.producer.Producer;

public class KafkaProducerModule extends AbstractModule {
    @Override
    protected void configure() {
    }

    @Provides
    @Singleton
    public KafkaNotifier<PaymentApplication> getKafkaInterpreterPaymentApplication(KafkaProducer<PaymentApplication> kafkaProducer) {
        return new KafkaNotifier(kafkaProducer);
    }

    @Provides
    @Singleton
    public KafkaNotifier<ProductOrderNotification> getKafkaInterpreterProductOrder(KafkaProducer<ProductOrderNotification> kafkaProducer) {
        return new KafkaNotifier(kafkaProducer);
    }

    @Provides
    @Singleton
    public KafkaProducer<PaymentApplication> getKafkaPaymentProducer() {
        final Producer<String, byte[]> producer = KafkaFactory.createInstance();
        return new KafkaProducer<>(producer, PaymentApplication.class, ConstantsModel.ORGANIZATION_CREATED_NOTIFICATION_TOPIC);
    }

    @Provides
    @Singleton
    public KafkaProducer<ProductOrderNotification> getKafkaProductOrderProducer() {
        final Producer<String, byte[]> producer = KafkaFactory.createInstance();
        return new KafkaProducer<>(producer, ProductOrderNotification.class, ConstantsModel.PRODUCT_ORDER_CREATED_TOPIC);
    }
}
