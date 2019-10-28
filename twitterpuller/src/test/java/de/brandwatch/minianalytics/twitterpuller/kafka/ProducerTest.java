package de.brandwatch.minianalytics.twitterpuller.kafka;

import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.Instant;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsEqual.equalTo;

@Testcontainers
@ContextConfiguration(initializers = {ProducerTest.Initializer.class})
@ExtendWith(SpringExtension.class)
@SpringBootTest
@TestPropertySource(properties = {"spring.autoconfigure.exclude=TweetProducer.class"})
public class ProducerTest {

    private static final Logger logger = LoggerFactory.getLogger(ProducerTest.class);

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
    private Producer producer;

    @Test
    public void testKafkaProducer() {

        KafkaConsumer<String, String> consumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "test-consumer",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
                ),
                new StringDeserializer(),
                new StringDeserializer()
        );

        consumer.subscribe(Collections.singleton("resources"));

        Resource resource = new Resource();
        resource.setAuthor("Max Leopold");
        resource.setText("Hello World");
        resource.setDate(Instant.now());

        producer.send(resource);

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, String> records = consumer.poll(Duration.ofSeconds(1));

            if (records.isEmpty()) {
                return false;
            }

            records.records("resources").forEach(x -> {
                try {
                    JSONObject retrievedResource = new JSONObject(x.value());
                    assertThat(retrievedResource.get("author"), is(equalTo("Max Leopold")));
                    assertThat(retrievedResource.get("text"), is(equalTo("Hello World")));
                } catch (JSONException e) {
                    logger.warn(e.getMessage());
                }
            });
            return true;
        });
    }
}
