package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.model.Mention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

import java.time.Instant;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    private KafkaTemplate<String, Mention> kafkaTemplate;

    @Autowired
    public Producer(KafkaTemplate<String, Mention> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void send(Mention mention){
        logger.info("sending message='{}", mention);
        kafkaTemplate.send("mentions",String.valueOf(Instant.now()), mention);
    }
}
