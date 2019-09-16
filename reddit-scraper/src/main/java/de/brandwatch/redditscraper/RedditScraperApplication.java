package de.brandwatch.redditscraper;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.reddit.RedditScraper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedditScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedditScraperApplication.class, args);
    }

    @Bean(initMethod = "scrapeReddit")
    public RedditScraper redditScraper(Producer producer) {
        return new RedditScraper(producer);
    }

}
