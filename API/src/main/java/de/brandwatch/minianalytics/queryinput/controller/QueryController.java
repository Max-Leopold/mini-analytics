package de.brandwatch.minianalytics.queryinput.controller;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.queryinput.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;


@org.springframework.web.bind.annotation.RestController
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    @Autowired
    private QueryService queryService;

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @PostMapping(value = "/queries")
    public ResponseEntity query(@RequestParam String query){
        logger.info("API Call: query");
        logger.info("Query: " + query);

        return queryService.createQuery(query);

    }

    @GetMapping(value = "/queries")
    public ResponseEntity queries(){
        logger.info("API Call: queries");
        return queryService.getAllQueries();
    }

    @GetMapping(value = "/queries/{queryID}")
    public ResponseEntity singleQuery(@PathVariable("queryID") String queryID){
        return queryService.getQueryByID(queryID);
    }


}
