package de.brandwatch.minianalytics.twitterpuller.twitter;

import org.springframework.context.annotation.Bean;
import twitter4j.TwitterStream;

public class TweetProducer {

    private TwitterStream twitterStream;

    public TweetProducer(TwitterStream twitterStream) {
        this.twitterStream = twitterStream;
    }

    public void sendTweetsToKafka() {
        twitterStream.sample("de");
    }

    @Bean(initMethod = "sendTweetsToKafka")
    public TweetProducer tweetProducer(TwitterStream twitterStream){
        return new TweetProducer(twitterStream);
    }
}
