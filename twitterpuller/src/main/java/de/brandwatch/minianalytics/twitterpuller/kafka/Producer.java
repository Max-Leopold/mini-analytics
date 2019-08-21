package de.brandwatch.minianalytics.twitterpuller.kafka;

import de.brandwatch.minianalytics.twitterpuller.model.Ressource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Ressource> kafkaTemplate;

    public void send(Ressource ressource){

        logger.info("sending message='{}'", ressource.toString());
        kafkaTemplate.send("twitter", String.valueOf(System.currentTimeMillis()), mention);
    }
}
