package module;

import avro.PaymentApplication.PaymentApplication;
import avro.ProductOrderNotification.ProductOrderNotification;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;

import configuration.Configuration;
import dataloader.urlschema.EntityHttpVars;
import dataloader.urlschema.HttpVarsBuilder;
import kafka.KafkaFactory;
import kafka.KafkaProducer;
import models.ConstantsModel;
import org.apache.kafka.clients.producer.Producer;

public class LoaderModule extends AbstractModule {
    @Override
    protected void configure() {
        EntityHttpVars entityHttpVars = HttpVarsBuilder.build(Configuration.current().httpVarsMap);
        bind(EntityHttpVars.class).toInstance(entityHttpVars);
        bind(Producer.class).toInstance(KafkaFactory.createInstance());
    }


}
