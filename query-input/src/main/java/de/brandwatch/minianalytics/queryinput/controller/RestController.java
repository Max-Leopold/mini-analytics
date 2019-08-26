package de.brandwatch.minianalytics.queryinput.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.queryinput.postgres.model.Query;
import de.brandwatch.minianalytics.queryinput.postgres.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;


@org.springframework.web.bind.annotation.RestController
public class RestController {

    Logger logger = LoggerFactory.getLogger(RestController.class);

    @Autowired
    QueryRepository queryRepository;

    static final private Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @RequestMapping(value = "/queries", method = RequestMethod.POST)
    public ResponseEntity query(@RequestParam String query){
        logger.info("API Call: query");
        logger.info("Query: " + query);

        //TODO parse Query into Lucene Query
        try {
            Query saveQuery = new Query(query);
            queryRepository.save(saveQuery);

            logger.info("Retrieved query: " + query);
            return ResponseEntity.status(200).body(gson.toJson(query));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }

    }

    @RequestMapping(value = "/queries", method = RequestMethod.GET)
    public ResponseEntity queries(){
        logger.info("API Call: queries");
        try {
            return ResponseEntity.status(200).body(gson.toJson(queryRepository.findAll()));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }
    }

    @RequestMapping(value = "/queries/{queryID}", method = RequestMethod.GET)
    public ResponseEntity singleQuery(@PathVariable("queryID") String queryID){
        try{
            return ResponseEntity.status(200).body(gson.toJson(queryRepository.findById(Long.valueOf(queryID))));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }
    }


}
