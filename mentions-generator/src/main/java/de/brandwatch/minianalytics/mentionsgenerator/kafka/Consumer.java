package de.brandwatch.minianalytics.mentionsgenerator.kafka;

import de.brandwatch.minianalytics.mentionsgenerator.model.Resource;
import de.brandwatch.minianalytics.mentionsgenerator.service.LuceneService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final LuceneService luceneService;

    @Autowired
    public Consumer(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    @KafkaListener(topics = "resources")
    public void receive(Resource resource) {
        logger.info("received message='{}'", resource.toString());

        luceneService.writeToQ(resource);
    }
}

