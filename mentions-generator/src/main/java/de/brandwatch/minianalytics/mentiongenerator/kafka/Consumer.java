package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import de.brandwatch.minianalytics.mentiongenerator.service.LuceneService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final LuceneService luceneService;

    @Autowired
    public Consumer(LuceneService luceneService) {
        this.luceneService = luceneService;
    }

    @KafkaListener(topics = "resources")
    public void receive(Resource resource) throws ParseException, IOException {
        logger.info("received message='{}'", resource.toString());

        luceneService.indexResource(resource);
    }
}

