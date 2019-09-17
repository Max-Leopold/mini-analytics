package de.brandwatch.redditscraper;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.reddit.RedditScraperForComments;
import de.brandwatch.redditscraper.reddit.RedditScraperForTitles;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class RedditScraperApplication {

    public static void main(String[] args) {
        SpringApplication.run(RedditScraperApplication.class, args);
    }

    @Bean(initMethod = "scrapeReddit")
    public RedditScraperForTitles redditScraperForTitles(Producer producer, RedditScraperForComments redditScraperForComments) {
        return new RedditScraperForTitles(producer, redditScraperForComments);
    }

    @Bean
    public RedditScraperForComments redditScraperForComments(Producer producer){
        return new RedditScraperForComments(producer);
    }
}
