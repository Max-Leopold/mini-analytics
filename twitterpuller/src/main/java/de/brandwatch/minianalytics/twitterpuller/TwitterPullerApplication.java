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

    @Autowired
    Producer producer;

    @PostConstruct
    public void sendTweetsToKafka(){
        TweetFetcher tweetFetcher = new TweetFetcher();
        tweetFetcher.getTwitterStream(producer).sample();
    }
}
