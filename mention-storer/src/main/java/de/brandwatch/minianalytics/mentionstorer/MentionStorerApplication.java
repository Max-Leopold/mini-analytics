package de.brandwatch.minianalytics.mentionstorer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;


@SpringBootApplication
public class MentionStorerApplication {

    Logger logger = LoggerFactory.getLogger(MentionStorerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MentionStorerApplication.class, args);
    }
}
