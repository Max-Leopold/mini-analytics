package de.brandwatch.minianalytics.mentionsgenerator.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.brandwatch.minianalytics.mentionsgenerator.model.Mention;
import de.brandwatch.minianalytics.mentionsgenerator.model.Resource;
import de.brandwatch.minianalytics.mentionsgenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentionsgenerator.postgres.repository.QueryRepository;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Testcontainers
@ExtendWith(SpringExtension.class)
@SpringBootTest
@ContextConfiguration(initializers = {KafkaConsumerProducerTest.Initializer.class})
public class KafkaConsumerProducerTest {



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
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false",
                    "spring.jpa.hibernate.ddl-auto=update",
                    "spring.jpa.generate-ddl=true",
                    "spring.datasource.driverClassName=org.postgresql.Driver",
                    "spring.jpa.show-sql=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    private final QueryRepository queryRepository;

    @Autowired
    public KafkaConsumerProducerTest(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Test
    public void testKafkaConsumerProducer(){

        kafkaContainer.start();
        postgreSQLContainer.start();

        //Populate Database
        Query query = new Query();
        query.setQuery("Hello AND author:\"Max Leopold\"");
        queryRepository.save(query);

        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        KafkaConsumer<String, Mention> kafkaConsumer = new KafkaConsumer<>(
                ImmutableMap.of(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ConsumerConfig.GROUP_ID_CONFIG, "test-consumer",
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class
                ),
                new StringDeserializer(),
                new JsonDeserializer<>(Mention.class ,mapper, false)
        );

        KafkaProducer<String, Resource> kafkaProducer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                ),
                new StringSerializer(),
                new JsonSerializer<>(mapper)
        );

        kafkaConsumer.subscribe(Collections.singleton("mentions"));

        Resource resource = new Resource();
        Instant instant = Instant.now();
        resource.setText("Hello World");
        resource.setDate(instant);
        resource.setAuthor("Max Leopold");

        ProducerRecord<String, Resource> record = new ProducerRecord<>("resources", String.valueOf(Instant.now()), resource);

        kafkaProducer.send(record);

        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            ConsumerRecords<String, Mention> records = kafkaConsumer.poll(Duration.ofSeconds(1));

            if (records.isEmpty()) {
                return false;
            }

            records.records("mentions").forEach(x -> {

                    assertThat(x.value().getAuthor(), is(equalTo("Max Leopold")));
                    assertThat(x.value().getText(), is(equalTo("Hello World")));
                    assertThat(String.valueOf(x.value().getQueryID()), is(equalTo("1")));
                DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                        .withZone(ZoneId.of("UTC"));
                    assertThat(x.value().getDate().toString(), is(equalTo(dateTimeFormatter.format(instant))));

            });
            return true;
        });

    }
}
