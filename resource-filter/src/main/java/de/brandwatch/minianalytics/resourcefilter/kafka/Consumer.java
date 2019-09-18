package de.brandwatch.minianalytics.resourcefilter.kafka;

import de.brandwatch.minianalytics.resourcefilter.model.Resource;
import de.brandwatch.minianalytics.resourcefilter.service.IndexService;
import org.apache.solr.client.solrj.SolrServerException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final IndexService indexService;

    private final Producer producer;

    @Autowired
    public Consumer(IndexService indexService, Producer producer) {
        this.indexService = indexService;
        this.producer = producer;
    }

    @KafkaListener(topics = "resources")
    public void receive(Resource resource) throws IOException, SolrServerException {
        logger.info("received message='{}'", resource.toString());

        if(indexService.isUniqueResource(resource)) producer.send(resource);
    }
}
