package de.brandwatch.minianalytics.mentiongenerator;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
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

    @PostConstruct
    public void sendMessagesOnKafka(){
        producer.send("Waz up?");
    }
}

