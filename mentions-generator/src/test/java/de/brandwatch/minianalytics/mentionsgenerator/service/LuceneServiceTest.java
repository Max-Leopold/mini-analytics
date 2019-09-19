package de.brandwatch.minianalytics.mentionsgenerator.service;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentiongenerator.model.Mention;
import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import de.brandwatch.minianalytics.mentiongenerator.postgres.cache.QueryCache;
import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentiongenerator.service.LuceneService;
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
        matched.setSourceTag("twitter");
        matched.setURL("abc.com");

        Resource notMatched = new Resource();
        notMatched.setAuthor("Max Leopold");
        notMatched.setDate(Instant.now());
        notMatched.setText("Hello World");
        notMatched.setURL("abcd.com");
        notMatched.setSourceTag("twitter");

        Mention mention = new Mention();
        mention.setDate(matched.getDate());
        mention.setText(matched.getText());
        mention.setAuthor(matched.getAuthor());
        mention.setQueryID(0);
        mention.setSourceTag("twitter");
        mention.setURL("abc.com");

        luceneService.writeToQ(matched);
        luceneService.writeToQ(notMatched);
        luceneService.indexQ();

        verify(producer, times(1)).send(mention);
    }
}
