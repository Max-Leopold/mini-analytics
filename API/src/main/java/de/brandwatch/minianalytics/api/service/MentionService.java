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

    public List<Mention> getMentionsFromQueryID(String queryID, String startDate, String endDate) {

        StringBuilder sb = new StringBuilder();
        sb.append("[");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        if (startDate.equals("")) {
            sb.append("*");
        } else {
            String convertedDate = startDate + " 00:00:00";
            TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
            Instant instant = Instant.from(zonedDateTime);

            sb.append(instant);
        }

        sb.append(" TO ");

        if(endDate.equals("")){
            sb.append("*");
        }else{
            String convertedDate = endDate + " 00:00:00";
            TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));
            Instant instant = Instant.from(zonedDateTime);

            sb.append(instant);
        }

        sb.append("]");

        return mentionRepository.findMentionsAfterDate(Long.parseLong(queryID), sb.toString());
    }
}
