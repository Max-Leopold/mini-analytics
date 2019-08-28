package de.brandwatch.minianalytics.api;

import de.brandwatch.minianalytics.queryinput.solr.model.Mention;
import de.brandwatch.minianalytics.queryinput.solr.repository.MentionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;
import java.time.Instant;

@SpringBootApplication
public class APIApplication {

    public static void main(String[] args) {
        SpringApplication.run(APIApplication.class, args);
    }

    @Autowired
    MentionRepository mentionRepository;

    @PostConstruct
    public void test(){
        Mention mention = new Mention();

        mention.setText("Hello World");
        mention.setAuthor("Max Leopold");
        mention.setQueryID(1);
        mention.setDate(Instant.now());

    }
}
