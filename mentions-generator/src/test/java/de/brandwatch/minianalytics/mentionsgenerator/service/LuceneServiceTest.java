package de.brandwatch.minianalytics.mentionsgenerator.service;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentiongenerator.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.mentiongenerator.service.LuceneService;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LuceneServiceTest {


    @Mock
    private QueryRepository queryRepository;

    @Mock
    private Producer producer;

    @InjectMocks
    private LuceneService luceneService;

    @Test
    void testLuceneIndexingAndSearch() throws IOException, ParseException {

        Query query = new Query("Hello AND author:\"Maximilian Leopold\"");
        List<Query> queries = new ArrayList<>();
        queries.add(query);

        when(queryRepository.findAll()).thenReturn(queries);

        Resource noMatch = new Resource();
        noMatch.setText("Hallo Welt");
        noMatch.setAuthor("Maximilian Leopold");
        noMatch.setDate(Instant.now());

        luceneService.indexResource(noMatch);
        verify(producer, times(0)).send(any());

        Resource match = new Resource();
        match.setText("Hello World");
        match.setAuthor("Maximilian Leopold");
        match.setDate(Instant.now());

        luceneService.indexResource(match);
        verify(producer, times(1)).send(any());

    }
}
