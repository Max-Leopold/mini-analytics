package de.brandwatch.minianalytics.mentionstorer.kafka;

import de.brandwatch.minianalytics.library.solr.model.Mention;
import de.brandwatch.minianalytics.library.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    private MentionRepository mentionRepository;

    @KafkaListener(topics = "mentions")
    public void receive(Mention mention) {
        logger.info("received message='{}'", mention);

        //Save Mention in Solr db
        mentionRepository.save(mention);
    }
}
