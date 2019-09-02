package de.brandwatch.minianalytics.twitterpuller.twitter;

import twitter4j.TwitterStream;

public class TweetProducer {

    private TwitterStream twitterStream;

    public TweetProducer(TwitterStream twitterStream) {
        this.twitterStream = twitterStream;
    }

    public void sendTweetsToKafka() {
        twitterStream.sample("de");
    }
}
