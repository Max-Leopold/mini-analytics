package de.brandwatch.minianalytics.mentiongenerator.postgres.cache;

import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentiongenerator.postgres.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class QueryCache {

    @Autowired
    QueryRepository queryRepository;

    @Cacheable("{queryID, query}")
    public List<Query> getCachedQueries(){
        return queryRepository.findAll();
    }


}
