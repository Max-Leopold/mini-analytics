package de.brandwatch.minianalytics.mentiongenerator.kafka;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void send(String message){
        logger.info("sending message='{}", message);
        kafkaTemplate.send("mentions-topic","Hidwdae", message);
    }
}
