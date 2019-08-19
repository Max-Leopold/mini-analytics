package de.brandwatch.minianalytics.mentionstorer.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @KafkaListener(topics = "mentions")
    public void receive(String message){
        logger.info("received message='{}'", message);
    }
}
