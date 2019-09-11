package de.brandwatch.minianalytics.mentiongenerator.postgres.cache;

import de.brandwatch.minianalytics.library.postgres.model.Query;
import de.brandwatch.minianalytics.library.postgres.repository.QueryRepository;
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
