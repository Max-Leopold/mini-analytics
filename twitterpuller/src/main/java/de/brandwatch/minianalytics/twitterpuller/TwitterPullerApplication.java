package de.brandwatch.minianalytics.twitterpuller;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.twitter.TweetFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TwitterPullerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterPullerApplication.class, args);
    }

    private TweetFetcher tweetFetcher;

    @Autowired
    Producer producer;

    @Autowired
    public TwitterPullerApplication(TweetFetcher tweetFetcher) {
        this.tweetFetcher = tweetFetcher;
    }

    @PostConstruct
    public void sendTweetsToKafka() {
        tweetFetcher.getTwitterStream().sample("de");
    }
}
