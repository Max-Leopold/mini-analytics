package de.brandwatch.minianalytics.mentionstorer.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import de.brandwatch.minianalytics.mentionstorer.model.Mention;
import de.brandwatch.minianalytics.mentionstorer.solr.repository.MentionRepository;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.apache.kafka.common.serialization.StringSerializer;
import org.junit.jupiter.api.Test;
import org.rnorth.ducttape.unreliables.Unreliables;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.KafkaContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.shaded.com.google.common.collect.ImmutableMap;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

@Testcontainers
@SpringBootTest
@ContextConfiguration(initializers = {KafkaConsumerTest.Initializer.class})
public class KafkaConsumerTest {

    private static final Logger logger = LoggerFactory.getLogger(KafkaConsumerTest.class);

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues.of(
                    "kafka.bootstrap-servers=" + kafkaContainer.getBootstrapServers(),
                    "spring.data.solr.host=http://127.0.0.1:" +
                            solrCloudModeContainer.getMappedPort(8983) +
                            "/solr"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    //Kafka Conainer
    @Container
    private static KafkaContainer kafkaContainer = new KafkaContainer();

    //Solr Cloud Mode
    private static ImageFromDockerfile solrCloudMode = new ImageFromDockerfile()
            .withDockerfileFromBuilder(dockerfileBuilder ->
                    dockerfileBuilder.from("solr:latest")
                            .entryPoint("exec solr -c -f"));

    @Container
    private static GenericContainer solrCloudModeContainer = new GenericContainer(solrCloudMode)
            .withNetworkAliases("solr1")
            .waitingFor(Wait.forHttp("/solr/admin/collections?action=CREATE&name=mentions&numShards=1&replicationFactor=1"));

    @Autowired
    MentionRepository mentionRepository;

    @Test
    public synchronized void kafkaConsumerTest() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JavaTimeModule());
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);

        KafkaProducer<String, Mention> kafkaProducer = new KafkaProducer<>(
                ImmutableMap.of(
                        ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers(),
                        ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class,
                        ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class
                ),
                new StringSerializer(),
                new JsonSerializer<>(mapper)
        );

        String createCollectionURL = "http://localhost:" +
                solrCloudModeContainer.getMappedPort(8983) +
                "/solr/admin/collections?action=CREATE&name=mentions&numShards=1&replicationFactor=1";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(createCollectionURL, String.class, null);


        Mention mention = new Mention();
        Instant instant = Instant.now();
        mention.setDate(instant);
        mention.setAuthor("Max Leopold");
        mention.setQueryID(1);
        mention.setText("Hello World");

        ProducerRecord<String, Mention> record = new ProducerRecord<>(
                "mentions",
                String.valueOf(Instant.now()),
                mention);

        kafkaProducer.send(record);


        Unreliables.retryUntilTrue(10, TimeUnit.SECONDS, () -> {
            if(mentionRepository.getMentions().size() == 0) {
                return false;
            }

            Mention receivedMention = mentionRepository.getMentions().get(0);
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")
                    .withZone(ZoneId.of("UTC"));
            assertThat(receivedMention.getAuthor(),
                    is(equalTo(mention.getAuthor())));
            assertThat(receivedMention.getQueryID(),
                    is(equalTo(mention.getQueryID())));
            assertThat(receivedMention.getText(),
                    is(equalTo(mention.getText())));
            assertThat(dateTimeFormatter.format(receivedMention.getDate()),
                    is(equalTo(dateTimeFormatter.format(mention.getDate()))));
            return true;
        });
    }

}
