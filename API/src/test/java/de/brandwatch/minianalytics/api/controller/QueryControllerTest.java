package de.brandwatch.minianalytics.api.controller;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.service.QueryService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;

@ExtendWith(SpringExtension.class)
@SpringBootTest
@AutoConfigureMockMvc
public class QueryControllerTest {

    @Autowired
    MockMvc mockMvc;

    @MockBean
    QueryService queryService;

    @Test
    public void testQueryCreation() throws Exception {
        Query query = new Query("Hello AND World");
        when(queryService.createQuery("Hello AND World")).thenReturn(query);

        mockMvc.perform(post("/queries")
                .param("query", "Hello AND World"))
                .andExpect(jsonPath("$.query", is(equalTo("Hello AND World"))))
                .andDo(print());
    }

    @Test
    public void testGetAllQueries() throws Exception {
        Query query = new Query("Hello AND World");
        List<Query> queries = new ArrayList<>();
        queries.add(query);
        when(queryService.getAllQueries()).thenReturn(queries);

        mockMvc.perform(get("/queries"))
                .andExpect(jsonPath("$[0].query", is(equalTo("Hello AND World"))))
                .andDo(print());
    }

    @Test
    public void testGetSingleQueries() throws Exception {
        Query query = new Query("Hello AND World");
        when(queryService.getQueryByID("0")).thenReturn(java.util.Optional.of(query));

        mockMvc.perform(get("/queries/0"))
                .andExpect(jsonPath("$.value.query", is(equalTo("Hello AND World"))))
                .andDo(print());
    }
}
