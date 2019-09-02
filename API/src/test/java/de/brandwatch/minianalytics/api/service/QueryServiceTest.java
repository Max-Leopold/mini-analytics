package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;


@ExtendWith(MockitoExtension.class)
public class QueryServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    private QueryService queryService;

    @Test
    void testQueryGeneration(){
        String queryString = "Hello AND author:\"Max Leopold\"";
        Query expectedQuery = new Query(queryString);

        queryService.createQuery(queryString);

        verify(queryRepository).save(eq(expectedQuery));
    }
}
