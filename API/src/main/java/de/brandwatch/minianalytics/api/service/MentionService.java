package de.brandwatch.minianalytics.api.service;

import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@Component
public class MentionService {

    private static final Logger logger = LoggerFactory.getLogger(MentionService.class);

    private final MentionRepository mentionRepository;

    private static final Gson gson = new Gson();

    @Autowired
    public MentionService(MentionRepository mentionRepository) {
        this.mentionRepository = mentionRepository;
    }

    public List<Mention> getMentionsFromQueryID(String queryID, String date){

        List<Mention> mentions = mentionRepository.findByQueryID(Long.parseLong(queryID));

        if(!date.equals("")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String convertedDate = date + " 00:00:00";
            TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
            Instant instant = Instant.from(zonedDateTime);

            String dateBounds = "[" + instant + " TO *]";

            logger.info("queryID: *?0* AND date: *?1*");

            return mentionRepository.findMentionsAfterDate(Long.parseLong(queryID), dateBounds);
        }
        return mentions;
    }
}
