package de.brandwatch.minianalytics.twitterpuller;

import de.brandwatch.minianalytics.twitterpuller.twitter.TweetProducer;
import de.brandwatch.minianalytics.twitterpuller.twitter.TwitterPullerStatusListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import twitter4j.Twitter;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;

import java.time.Instant;

@SpringBootApplication
@EnableScheduling
@EnableCaching
public class TwitterPullerApplication {

    private static final Logger logger = LoggerFactory.getLogger(TwitterPullerApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(TwitterPullerApplication.class, args);
    }

    @Bean
    public TwitterStream getTwitterStream(TwitterPullerStatusListener listener) {
        TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(listener);
        return twitterStream;
    }

    @Bean
    public Twitter twitter(){
        return TwitterFactory.getSingleton();
    }

    @Bean(initMethod = "sendTweetsToKafka")
    public TweetProducer tweetProducer(TwitterStream twitterStream) {
        return new TweetProducer(twitterStream);
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("{queryID, query}");
    }

    //Refresh Cache every 10 minutes
    @CacheEvict(allEntries = true, cacheNames = "{queryID, query}")
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void reportCacheEvict() {
        logger.info("Flush Cache " + Instant.now());
    }
}
