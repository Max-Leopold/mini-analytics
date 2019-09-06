package de.brandwatch.minianalytics.api.service;

import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.*;
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

        if(!endDate.equals("") && !startDate.equals("")){
            if(!validateDates(convertStringDateToInstant(startDate), convertStringDateToInstant(endDate))){
                throw new DateTimeException("Dates are not in the right order");
            }
        }

        if (startDate.equals("")) {
            sb.append("*");
        } else {
            sb.append(convertStringDateToInstant(startDate));
        }

        sb.append(" TO ");

        if (endDate.equals("")) {
            sb.append("*");
        } else {
            sb.append(convertStringDateToInstant(endDate));
        }

        sb.append("]");

        return mentionRepository.findMentionsAfterDate(Long.parseLong(queryID), sb.toString());
    }

    private Instant convertStringDateToInstant(String date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String convertedDate = date + " 00:00:00";
        TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
        LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
        ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.of("UTC"));

        return Instant.from(zonedDateTime);
    }


    private boolean validateDates(Instant startDate, Instant endDate) {
        return !startDate.isAfter(endDate);
    }
}
