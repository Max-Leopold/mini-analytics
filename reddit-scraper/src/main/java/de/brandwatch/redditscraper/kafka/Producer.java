package de.brandwatch.redditscraper.kafka;

import de.brandwatch.redditscraper.model.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Resource> kafkaTemplate;

    public void send(Resource resource){

        logger.info("sending message='{}'", resource.toString());
        kafkaTemplate.send("reddit", String.valueOf(System.currentTimeMillis()), resource);
    }
}
