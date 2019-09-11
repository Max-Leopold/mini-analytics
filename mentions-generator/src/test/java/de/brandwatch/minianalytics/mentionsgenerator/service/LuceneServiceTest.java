package de.brandwatch.minianalytics.mentionsgenerator.service;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import de.brandwatch.minianalytics.mentiongenerator.postgres.cache.QueryCache;
import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentiongenerator.service.LuceneService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@ComponentScan("de.brandwatch.minianalytics.mentiongenerator.service")
public class LuceneServiceTest {

    @Configuration
    @EnableScheduling
    public class ScheduledConfig {
    }

    @Mock
    Producer producer;

    @Mock
    QueryCache queryCache;

    @SpyBean
    @InjectMocks
    LuceneService luceneService;

    @BeforeEach
    public void init() {

    }

    @Test
    void testLuceneIndexingAndSearch() throws IOException, ParseException {

        Query query = new Query("Hello AND author:\"Maximilian Leopold\"");
        List<Query> queries = new ArrayList<>();
        queries.add(query);

        when(queryCache.getCachedQueries()).thenReturn(queries);

        Resource matched = new Resource();
        matched.setAuthor("Maximilian Leopold");
        matched.setDate(Instant.now());
        matched.setText("Hello World");

        Resource notMatched = new Resource();
        notMatched.setAuthor("Max Leopold");
        notMatched.setDate(Instant.now());
        notMatched.setText("Hello World");

        luceneService.writeToQ(matched);
        luceneService.writeToQ(notMatched);
        luceneService.indexQ();

        verify(producer, times(1)).send(any());
    }
}
