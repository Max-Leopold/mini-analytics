package de.brandwatch.minianalytics.twitterpuller.postgres.cache;

import de.brandwatch.minianalytics.twitterpuller.postgres.model.Query;
import de.brandwatch.minianalytics.twitterpuller.postgres.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryCache {

    private final QueryRepository queryRepository;

    @Autowired
    public QueryCache(QueryRepository queryRepository) {
        this.queryRepository = queryRepository;
    }

    @Cacheable("{queryID, query}")
    public List<Query> getCachedQueries(){
        return queryRepository.findAll();
    }
}
