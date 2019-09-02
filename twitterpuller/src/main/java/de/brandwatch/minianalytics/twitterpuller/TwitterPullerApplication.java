package de.brandwatch.minianalytics.twitterpuller;

import de.brandwatch.minianalytics.twitterpuller.twitter.TweetProducer;
import de.brandwatch.minianalytics.twitterpuller.twitter.TwitterPullerStatusListener;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

@SpringBootApplication
public class TwitterPullerApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwitterPullerApplication.class, args);
    }


    @Bean
    public TwitterStream getTwitterStream(TwitterPullerStatusListener listener) {
        TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(listener);
        return twitterStream;
    }

    @Bean(initMethod = "sendTweetsToKafka")
    public TweetProducer tweetProducer(TwitterStream twitterStream){
        return new TweetProducer(twitterStream);
    }
}
