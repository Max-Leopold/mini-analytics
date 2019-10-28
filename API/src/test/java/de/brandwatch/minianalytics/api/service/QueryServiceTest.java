package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.api.security.model.User;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
public class QueryServiceTest {

    @Mock
    private QueryRepository queryRepository;

    @InjectMocks
    @Spy
    private QueryService queryService;

    @Test
    void testQueryGeneration(){
        User user = new User();
        user.setUsername("Max");
        user.setPassword("pw");
        user.setRoles(null);
        user.setId(1L);

        String queryString = "Hello AND author:\"Max Leopold\"";
        Query expectedQuery = new Query(queryString, user);

        when(queryService.getUserFromSecurityContext()).thenReturn(user);

        queryService.createQuery(queryString);

        verify(queryRepository).save(eq(expectedQuery));
    }
}
