package de.brandwatch.minianalytics.api.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.api.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;


@RestController
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @PostMapping(value = "/queries")
    public ResponseEntity query(@RequestParam String query){
        try {
            logger.info("POST API Call: /query");
            logger.info("Query: " + query);

            return ResponseEntity.status(200).body(queryService.createQuery(query));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }

    }

    @GetMapping(value = "/queries")
    public ResponseEntity queries(){
        logger.info("GET API Call: /queries");
        try{
            return ResponseEntity.status(200).body(queryService.getAllQueries());
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }

    @GetMapping(value = "/queries/{queryID}")
    public ResponseEntity singleQuery(@PathVariable("queryID") String queryID){
        logger.info("GET API Call: /queries/" + queryID);
        try{
            return ResponseEntity.status(200).body(queryService.getQueryByID(queryID));
        }catch (Exception e){
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }


}
