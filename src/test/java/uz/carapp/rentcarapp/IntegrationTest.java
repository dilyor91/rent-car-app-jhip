package uz.carapp.rentcarapp;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;
import uz.carapp.rentcarapp.config.AsyncSyncConfiguration;
import uz.carapp.rentcarapp.config.EmbeddedElasticsearch;
import uz.carapp.rentcarapp.config.EmbeddedKafka;
import uz.carapp.rentcarapp.config.EmbeddedSQL;
import uz.carapp.rentcarapp.config.JacksonConfiguration;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { RentcarappjhipApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
@EmbeddedKafka
public @interface IntegrationTest {
}
