package de.brandwatch.minianalytics.api.controller;


import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.service.MentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@RestController
public class MentionController {

    private static final Logger logger = LoggerFactory.getLogger(MentionController.class);

    private static final Gson gson = new Gson();

    private final MentionService mentionService;

    @Autowired
    public MentionController(MentionService mentionService) {
        this.mentionService = mentionService;
        LocalDate.now().minusDays(1).toString();
    }

    @GetMapping(value = "/mentions/{queryID}")
    public ResponseEntity findMentionsFromQuery(
            @PathVariable String queryID,
            @RequestParam(value = "startDate", defaultValue = "")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", defaultValue = "")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try{
            if(startDate == null) startDate = LocalDateTime.now().minusDays(1);
            if(endDate == null) endDate = LocalDateTime.now();

            if(!validateDates(startDate, endDate)){
                throw new DateTimeException("Dates are not in the right order");
            }

            return ResponseEntity.status(200).body(gson.toJson(mentionService.getMentionsFromQueryID(queryID,
                    startDate.toInstant(ZoneOffset.UTC),
                    endDate.toInstant(ZoneOffset.UTC))));
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }

    private boolean validateDates(LocalDateTime startDate, LocalDateTime endDate) {
        return !startDate.isAfter(endDate);
    }
}
