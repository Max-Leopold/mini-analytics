package de.brandwatch.minianalytics.api;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.api.security.model.User;
import de.brandwatch.minianalytics.api.util.PostgresClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@ContextConfiguration(initializers = {QueryControllerIntegrationTest.Initializer.class})
@AutoConfigureMockMvc
@SpringBootTest
public class QueryControllerIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(QueryControllerIntegrationTest.class);

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues.of(
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false",
                    "spring.jpa.hibernate.ddl-auto=update",
                    "spring.jpa.generate-ddl=true",
                    "spring.datasource.driverClassName=org.postgresql.Driver",
                    "spring.jpa.show-sql=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    QueryRepository queryRepository;

    private User user;

    private static PostgresClient postgresClient;

    @BeforeAll
    public static void init() throws SQLException {
        postgresClient = new PostgresClient(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getPassword(),
                postgreSQLContainer.getUsername());
    }

    @BeforeEach
    public void beforeEach() throws SQLException {
        user = new User("username",
                bCryptPasswordEncoder.encode("password"),
                new HashSet<>());
        user.setId(1L);
        postgresClient.inserUser(user);

        UsernamePasswordAuthenticationToken authReq
                = new UsernamePasswordAuthenticationToken("username", "password");
        Authentication auth = authenticationManager.authenticate(authReq);
        SecurityContext sc = SecurityContextHolder.getContext();
        sc.setAuthentication(auth);
    }

    @AfterEach
    public void afterEach() throws SQLException {
        postgresClient.deleteAll();
    }

    @Test
    public void integrationTestCreateQuery() throws Exception {
        Query expectedQuery = new Query("Hello World AND author: \"Max Leopold\"", user);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/queries")
                .param("query", "Hello World AND author: \"Max Leopold\""))
                .andExpect(status().isOk())
                .andDo(print());

        assertThat(expectedQuery.getQuery(), is(equalTo(postgresClient.findAllQueries().get(0).getQuery())));
    }

    @Test
    public void integrationTestGetAllQueries() throws Exception {
        Query expectedQuery = new Query("Hello World AND author: \"Max Leopold\"", user);
        postgresClient.insertQuery(expectedQuery);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/queries"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$[0].query", is(equalTo(expectedQuery.getQuery()))));
    }

    @Test
    public void integrationTestGetQueryByID() throws Exception {
        Query expectedQuery = new Query("Hello World AND author: \"Max Leopold\"", user);
        expectedQuery.setQueryID(1L);
        postgresClient.insertQuery(expectedQuery);

        mockMvc.perform(MockMvcRequestBuilders
                .get("/queries/1"))
                .andExpect(status().isOk())
                .andDo(print())
                .andExpect(jsonPath("$.query", is(equalTo(expectedQuery.getQuery()))));
    }
}
