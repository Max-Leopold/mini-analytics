package de.brandwatch.minianalytics.api;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.security.model.User;
import de.brandwatch.minianalytics.api.solr.model.Mention;
import de.brandwatch.minianalytics.api.util.PostgresClient;
import de.brandwatch.minianalytics.api.util.SolrClient;
import org.hamcrest.core.Is;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.util.TestPropertyValues;
import org.springframework.context.ApplicationContextInitializer;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.web.client.RestTemplate;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.wait.strategy.Wait;
import org.testcontainers.images.builder.ImageFromDockerfile;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.sql.SQLException;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

@Testcontainers
@ContextConfiguration(initializers = {MentionControllerIntegrationTest.Initializer.class})
@AutoConfigureMockMvc
@SpringBootTest
public class MentionControllerIntegrationTest {

    static class Initializer
            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {

            TestPropertyValues.of(
                    "spring.data.solr.host=http://127.0.0.1:" +
                            solrCloudModeContainer.getMappedPort(8983) +
                            "/solr",
                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
                    "spring.datasource.password=" + postgreSQLContainer.getPassword(),
                    "spring.jpa.properties.hibernate.temp.use_jdbc_metadata_defaults = false",
                    "spring.jpa.hibernate.ddl-auto=none",
                    "spring.jpa.generate-ddl=true",
                    "spring.datasource.driverClassName=org.postgresql.Driver",
                    "spring.jpa.show-sql=true"
            ).applyTo(configurableApplicationContext.getEnvironment());
        }
    }

    //Solr Cloud Mode
    private static ImageFromDockerfile solrCloudMode = new ImageFromDockerfile()
            .withDockerfileFromBuilder(dockerfileBuilder ->
                    dockerfileBuilder.from("solr:latest")
                            .entryPoint("exec solr -c -f"));

    @Container
    private static GenericContainer solrCloudModeContainer = new GenericContainer(solrCloudMode)
            .withNetworkAliases("solr1")
            .waitingFor(Wait.forHttp("/solr/admin/collections?action=CREATE&name=mentions&numShards=1&replicationFactor=1"));

    @Container
    private static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer();

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private AuthenticationManager authenticationManager;

    private User user;

    private static PostgresClient postgresClient;

    private SolrClient solrClient;

    @BeforeAll
    public static void init() throws SQLException {
        postgresClient = new PostgresClient(
                postgreSQLContainer.getJdbcUrl(),
                postgreSQLContainer.getPassword(),
                postgreSQLContainer.getUsername());
    }

    @BeforeEach
    public void beforeEach() throws SQLException {

        String createCollectionURL = "http://localhost:" +
                solrCloudModeContainer.getMappedPort(8983) +
                "/solr/admin/collections?action=CREATE&name=mentions&numShards=1&replicationFactor=1";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(createCollectionURL, String.class, null);

        solrClient = new SolrClient("http://localhost:" + solrCloudModeContainer.getMappedPort(8983) + "/solr");

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

        String createCollectionURL = "http://localhost:" +
                solrCloudModeContainer.getMappedPort(8983) +
                "/solr/admin/collections?action=DELETE&name=mentions";

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(createCollectionURL, String.class, null);
    }

    @Test
    public void integrationTestMentionController() throws Exception {

        Mention mention = new Mention();
        Instant instant = Instant.now();
        mention.setDate(instant);
        mention.setAuthor("Max Leopold");
        mention.setQueryID(0);
        mention.setText("Hello World");

        solrClient.save(mention);

        Query expectedQuery = new Query("\"Hello World\" AND author: \"Max Leopold\"", user);

        postgresClient.insertQuery(expectedQuery);

        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'");

        mockMvc.perform(MockMvcRequestBuilders.get("/mentions/0"))
                .andDo(print())
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].queryID", Is.is(equalTo((int) mention.getQueryID()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].author", Is.is(equalTo(mention.getAuthor()))))
                .andExpect(MockMvcResultMatchers.jsonPath("$[0].text", Is.is(equalTo(mention.getText()))));
    }

}
