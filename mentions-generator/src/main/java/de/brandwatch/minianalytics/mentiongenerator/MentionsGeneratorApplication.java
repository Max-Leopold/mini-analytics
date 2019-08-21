package de.brandwatch.minianalytics.mentiongenerator;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Consumer;
import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentiongenerator.postgres.repository.QueryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class MentionsGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentionsGeneratorApplication.class, args);
    }

    @Autowired
    private Producer producer;

    @Autowired
    private Consumer consumer;

    @Autowired
    private QueryRepository queryRepository;

    @PostConstruct
    public void matchMentionsOnQueries(){


    }
}

