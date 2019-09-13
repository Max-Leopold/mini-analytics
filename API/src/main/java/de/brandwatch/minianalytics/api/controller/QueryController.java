package de.brandwatch.minianalytics.api.controller;

import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.postgres.model.Query;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.api.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@RestController
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private static final Gson gson = new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();

    private QueryService queryService;

    @Autowired
    public QueryController(QueryService queryService) {
        this.queryService = queryService;
    }

    @PostMapping(value = "/queries")
    public Query query(@RequestParam String query) {
        try {
            logger.info("POST /queries: {}", query);
            return queryService.createQuery(query);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Couldn't create Query.", e);
        }
    }

    @GetMapping(value = "/queries")
    public List<Query> queries() {
        try {
            logger.info("GET /queries");
            return queryService.getAllQueries();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
                    "Couldn't fetch Queries. Maybe there is a database problem.", e);
        }
    }

    @GetMapping(value = "/queries/{queryID}")
    public Query singleQuery(@PathVariable("queryID") String queryID) {
        try {
            logger.info("GET /queries/" + queryID);
            return queryService.getQueryByID(queryID)
                    .orElseThrow(() -> new ResponseStatusException(HttpStatus.NO_CONTENT, "No Query with ID " + queryID + " was found."));
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }
}
