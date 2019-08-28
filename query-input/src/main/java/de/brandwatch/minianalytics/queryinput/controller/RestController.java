package de.brandwatch.minianalytics.queryinput.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.queryinput.solr.model.Mention;
import de.brandwatch.minianalytics.queryinput.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.List;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    MentionRepository mentionRepository;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/send")
    public void send(@RequestParam String name) {

        //TODO parse Query into Lucene Query
        logger.info("Retrieved name: " + name);
    }

    @RequestMapping(value = "/mentions/{queryID}")
    public ResponseEntity findMentionsFromQuery(@PathVariable String queryID, @RequestParam(value = "date", defaultValue = "") String date){

        List<Mention> mentions = mentionRepository.findByQueryID(Long.parseLong(queryID));


        if(!date.equals("")){
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

            String convertedDate = date + " 00:00:00";
            TemporalAccessor temporalAccessor = formatter.parse(convertedDate);
            LocalDateTime localDateTime = LocalDateTime.from(temporalAccessor);
            ZonedDateTime zonedDateTime = ZonedDateTime.of(localDateTime, ZoneId.systemDefault());
            Instant instant = Instant.from(zonedDateTime);


            StringBuilder sb = new StringBuilder();
            sb.append("[");
            sb.append(instant);
            sb.append(" TO *]");

            logger.info("queryID: *?0* AND date: *?1*");

            return ResponseEntity.status(200).body(mentionRepository.findMentionsAfterDate(Long.parseLong(queryID), sb.toString()));
        }

        return ResponseEntity.status(200).body(mentions);
    }
}
