package de.brandwatch.minianalytics.twitterpuller;

import de.brandwatch.minianalytics.twitterpuller.twitter.TwitterPullerStatusListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import javax.annotation.PostConstruct;

@SpringBootApplication
public class TwitterPullerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterPullerApplication.class, args);
    }

    private final TwitterPullerStatusListener listener;

    @Autowired
    public TwitterPullerApplication(TwitterPullerStatusListener listener) {
        this.listener = listener;
    }

    @PostConstruct
    public void sendTweetsToKafka() {
        getTwitterStream(listener).sample("de");
    }

    @Bean
    public TwitterStream getTwitterStream(TwitterPullerStatusListener listener) {
        TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(listener);
        return twitterStream;
    }
}
