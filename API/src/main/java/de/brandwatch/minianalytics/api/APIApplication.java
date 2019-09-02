package de.brandwatch.minianalytics.api;

import de.brandwatch.minianalytics.queryinput.solr.repository.MentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
    }

    @Autowired
    MentionRepository mentionRepository;

}
