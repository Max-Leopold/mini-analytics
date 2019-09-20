package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;

@Component
public class MentionService {

    private static final Logger logger = LoggerFactory.getLogger(MentionService.class);

    private final MentionRepository mentionRepository;

    @Autowired
    public MentionService(MentionRepository mentionRepository) {
        this.mentionRepository = mentionRepository;
    }

    public List<Mention> getMentionsFromQueryID(String queryID, Instant startDate, Instant endDate) {
        return mentionRepository.findMentionsAfterDate(Long.parseLong(queryID), "[" + startDate + " TO " + endDate + "]");
    }
}
