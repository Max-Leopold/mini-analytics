package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.core.Is.is;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = {ConsumerTest.Initializer.class})
public class ConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(ConsumerTest.class);

    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer();

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
            TestPropertyValues.of(
                    "kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Autowired
    Consumer consumer;

    @Test
    public void testKafkaConsumer() throws ExecutionException, InterruptedException {

        kafkaContainer.start();
        postgreSQLContainer.start();

        KafkaProducer<String, Resource> kafkaProducer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                ),
                new StringSerializer(),
                new JsonSerializer<>()
        );

        ArgumentCaptor argumentCaptor = ArgumentCaptor.forClass(Resource.class);

        Resource resource = new Resource();
        resource.setText("Hello World");
        resource.setDate(Instant.now());
        resource.setAuthor("Max Leopold");

        ProducerRecord<String, Resource> record = new ProducerRecord<>("resources", String.valueOf(Instant.now()), resource);
        logger.warn(kafkaProducer.send(record).get().topic());

        //ConsumerRecord<String, Resource> consumerRecord = KafkaTestUtils.getSingleRecord(consumer, "resources");

        //Mockito.verify(luceneService, Mockito.times(1)).indexResource(Mockito.any(Resource.class));
        //consumer.getCountDownLatch().await(10000, TimeUnit.MILLISECONDS);
        consumer.getCountDownLatch().await(10000, TimeUnit.MILLISECONDS);
        assertThat(consumer.getCountDownLatch().getCount(), is(equalTo(0L)));

        kafkaContainer.close();
        postgreSQLContainer.close();
    }
}

