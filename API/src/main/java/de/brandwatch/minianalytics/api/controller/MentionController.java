package de.brandwatch.minianalytics.api.controller;


import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.service.MentionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

@RestController
public class MentionController {

    private static final Logger logger = LoggerFactory.getLogger(RestController.class);

    private static final Gson gson = new Gson();

    private final MentionService mentionService;

    @Autowired
    public MentionController(MentionService mentionService) {
        this.mentionService = mentionService;
    }

    @GetMapping(value = "/mentions/{queryID}")
    public ResponseEntity findMentionsFromQuery(@PathVariable String queryID, @RequestParam(value = "date", defaultValue = "") String date) {
        try{
            return ResponseEntity.status(200).body(gson.toJson(mentionService.getMentionsFromQueryID(queryID, date)));
        } catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }
}
