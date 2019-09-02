package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    private final QueryRepository queryRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    public Query createQuery(String query) {
        Query saveQuery = new Query(query);

        logger.info("Retrieved query: " + query);
        return queryRepository.save(saveQuery);
    }

    public List<Query> getAllQueries() {
        return queryRepository.findAll();
    }

    public Optional<Query> getQueryByID(String queryID) {
        return queryRepository.findById(Long.valueOf(queryID));
    }
}
