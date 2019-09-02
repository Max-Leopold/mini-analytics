package de.brandwatch.minianalytics.mentionsgenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Consumer;
import de.brandwatch.minianalytics.mentiongenerator.kafka.config.ConsumerConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@EmbeddedKafka
@SpringJUnitConfig(KafkaConsumerTest.Config.class)
@ContextConfiguration(classes = {ConsumerConfig.class})
public class KafkaConsumerTest {

    @Configuration
    static class Config {
    }

    @Autowired
    Consumer consumer;

    @Test
    public void test(){
        assertNotNull(consumer);
    }



    /*private static final Logger LOGGER =
            LoggerFactory.getLogger(KafkaConsumerTest.class);

    private static String RECEIVER_TOPIC = "sender.t";

    @Autowired
    EmbeddedKafkaBroker embeddedKafkaBroker;

    @Autowired
    Consumer consumer;

    private KafkaTemplate<String, Ressource> template;

    @Autowired
    private KafkaListenerEndpointRegistry kafkaListenerEndpointRegistry;

    @BeforeEach
    public void setUp() {

        Map<String, Object> senderProperties = KafkaTestUtils.senderProps(embeddedKafkaBroker.getBrokersAsString());

        ProducerFactory<String, Ressource> producerFactory = new DefaultKafkaProducerFactory<>(senderProperties);

        template = new KafkaTemplate<>(producerFactory);
        template.setDefaultTopic(RECEIVER_TOPIC);

        for (MessageListenerContainer messageListenerContainer : kafkaListenerEndpointRegistry
                .getListenerContainers()) {
            ContainerTestUtils.waitForAssignment(messageListenerContainer,
                    embeddedKafkaBroker.getPartitionsPerTopic());
        }
    }

    @Test
    public void testConsume() {
        Ressource ressource = new Ressource();
        ressource.setDate(Instant.now());
        ressource.setAuthor("Max Leopold");
        ressource.setText("Hello World");
    }*/


}
