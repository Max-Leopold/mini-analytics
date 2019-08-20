package de.brandwatch.minianalytics.mentiongenerator.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "twitter")
    public void receive(String message){
        logger.info("received message='{}'", message);
    }
}

