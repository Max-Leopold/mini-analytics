package de.brandwatch.minianalytics.api.controller;

import com.google.gson.Gson;
import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.service.QueryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;


@RestController
public class QueryController {

    private static final Logger logger = LoggerFactory.getLogger(QueryController.class);

    private static final Gson gson = new Gson();

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
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }

    @GetMapping(value = "/queries")
    public List<Query> queries() {
        try {
            logger.info("GET /queries");
            return queryService.getAllQueries();
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }

    @GetMapping(value = "/queries/{queryID}")
    public Optional<Query> singleQuery(@PathVariable("queryID") String queryID) {
        try {
            logger.info("GET /queries/" + queryID);
            logger.info(queryService.getQueryByID(queryID).toString());
            return queryService.getQueryByID(queryID);
        } catch (Exception e) {
            throw new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "", e);
        }
    }
}
