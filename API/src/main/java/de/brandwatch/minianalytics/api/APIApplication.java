package de.brandwatch.minianalytics.api;

import de.brandwatch.minianalytics.library.solr.config.SolrConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Import;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EntityScan(basePackages = {"de.brandwatch.minianalytics.library.postgres.model"})
@EnableJpaRepositories(basePackages = {"de.brandwatch.minianalytics.library.postgres.repository"})
@Import({SolrConfig.class})
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
    }
}
