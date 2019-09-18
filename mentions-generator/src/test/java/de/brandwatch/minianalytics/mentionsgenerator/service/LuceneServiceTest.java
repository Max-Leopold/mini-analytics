package de.brandwatch.minianalytics.mentionsgenerator.service;

import de.brandwatch.minianalytics.mentionsgenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentionsgenerator.model.Mention;
import de.brandwatch.minianalytics.mentionsgenerator.model.Resource;
import de.brandwatch.minianalytics.mentionsgenerator.postgres.cache.QueryCache;
import de.brandwatch.minianalytics.mentionsgenerator.postgres.model.Query;
import org.apache.lucene.queryparser.classic.ParseException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.IOException;
import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class LuceneServiceTest {

    @Mock
    private Producer producer;

    @Mock
    private QueryCache queryCache;

    @InjectMocks
    private LuceneService luceneService;

    @Test
    void testLuceneIndexingAndSearch() throws IOException, ParseException {

        List<Query> queries = Collections.singletonList(new Query("Hello AND author:\"Maximilian Leopold\""));

        when(queryCache.getCachedQueries()).thenReturn(queries);

        Resource matched = new Resource();
        matched.setAuthor("Maximilian Leopold");
        matched.setDate(Instant.now());
        matched.setText("Hello World");

        Resource notMatched = new Resource();
        notMatched.setAuthor("Max Leopold");
        notMatched.setDate(Instant.now());
        notMatched.setText("Hello World");

        Mention mention = new Mention();
        mention.setDate(matched.getDate());
        mention.setText(matched.getText());
        mention.setAuthor(matched.getAuthor());
        mention.setQueryID(0);

        luceneService.writeToQ(matched);
        luceneService.writeToQ(notMatched);
        luceneService.indexQ();

        verify(producer, times(1)).send(mention);
    }
}
