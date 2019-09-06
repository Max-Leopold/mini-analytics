package de.brandwatch.minianalytics.api.controller;


import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@RestController
public class MentionController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    private static final Gson gson = new Gson();

    private final MentionRepository mentionRepository;

    @Autowired
    public MentionController(MentionRepository mentionRepository) {
        this.mentionRepository = mentionRepository;
    }

    @GetMapping(value = "/mentions/{queryID}")
    public ResponseEntity findMentionsFromQuery(
            @PathVariable String queryID,
            @RequestParam(value = "date", defaultValue = "") String date){

        logger.info("GET /mentions/" + queryID);

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

            return ResponseEntity.status(200).body(gson.toJson(mentionRepository.findMentionsAfterDate(
                    Long.parseLong(queryID), dateBounds)));
        }
        return ResponseEntity.status(200).body(gson.toJson(mentions));
    }
}
