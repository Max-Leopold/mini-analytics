package de.brandwatch.minianalytics.mentionsgenerator.postgres.cache;

import de.brandwatch.minianalytics.mentiongenerator.postgres.cache.QueryCache;
import de.brandwatch.minianalytics.mentiongenerator.postgres.repository.QueryRepository;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.ArrayList;

import static org.mockito.Mockito.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class QueryCacheTest {

    @Configuration
    @EnableCaching
    static class Config {

        @Bean
        CacheManager cacheManager() {
            return new ConcurrentMapCacheManager("{queryID, query}");
        }

        @Bean
        QueryRepository queryRepository() {
            return mock(QueryRepository.class);
        }

        @Bean
        QueryCache queryCache(QueryRepository queryRepository) {
            return new QueryCache(queryRepository);
        }
    }

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    QueryCache queryCache;

    @Test
    public void testIfFindAllIsCalledOnceAndThenCached() {

        when(queryRepository.findAll()).thenReturn(new ArrayList<>());

        queryCache.getCachedQueries();
        queryCache.getCachedQueries();
        queryCache.getCachedQueries();

        verify(queryRepository, times(1)).findAll();
    }
}
