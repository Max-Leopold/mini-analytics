package de.brandwatch.minianalytics.mentionstorer;

import de.brandwatch.minianalytics.library.solr.config.SolrConfig;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({SolrConfig.class})
public class MentionStorerApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentionStorerApplication.class, args);
    }

}
