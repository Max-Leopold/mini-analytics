package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.model.Mention;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;

public class Producer {

    private static final Logger logger = LoggerFactory.getLogger(Producer.class);

    @Autowired
    private KafkaTemplate<String, Mention> kafkaTemplate;

    public Producer() {
    }

    public void send(Mention mention){
        logger.info("sending message='{}", mention);
        kafkaTemplate.send("mentions",String.valueOf(System.currentTimeMillis()), mention);
    }
}
