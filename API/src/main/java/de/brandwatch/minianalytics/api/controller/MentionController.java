package de.brandwatch.minianalytics.api.controller;


import com.google.common.base.Preconditions;
import de.brandwatch.minianalytics.api.service.MentionService;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.temporal.ChronoUnit;
import java.util.List;

@RestController
public class MentionController {

    private final MentionService mentionService;

    @Autowired
    public MentionController(MentionService mentionService) {
        this.mentionService = mentionService;
    }

    @GetMapping(value = "/mentions/{queryID}")
    public List<Mention> findMentionsFromQuery(
            @PathVariable String queryID,
            @RequestParam(value = "startDate", defaultValue = "")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam(value = "endDate", defaultValue = "")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        try {
            Instant parsedStartDate = parseOrDefault(startDate, 1);
            Instant parsedEndDate = parseOrDefault(endDate, 0);

            validateDates(parsedStartDate, parsedEndDate);

            return mentionService.getMentionsFromQueryID(queryID, parsedStartDate, parsedEndDate);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Couldn't fetch mentions for Query " + queryID, e);
        }
    }

    private void validateDates(Instant startDate, Instant endDate) {
        Preconditions.checkArgument(startDate.isBefore(Instant.now()),
                "startDate " + startDate + "is in the future");
        Preconditions.checkArgument(startDate.isBefore(endDate),
                "startDate " + startDate + " is not before endDate " + endDate);
    }

    private Instant parseOrDefault(LocalDateTime date, int minusDays) {
        return date == null ? Instant.now().minus(minusDays, ChronoUnit.DAYS) : date.toInstant(ZoneOffset.UTC);
    }
}
