package de.brandwatch.minianalytics.queryinput.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.queryinput.solr.repository.MentionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
    public ResponseEntity findMentionsFromQuery(@PathVariable String queryID){
        return ResponseEntity.status(200).body(mentionRepository.findByQueryID(Long.parseLong(queryID)));
    }
}
