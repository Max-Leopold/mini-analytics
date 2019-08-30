package de.brandwatch.minianalytics.twitterpuller.twitter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;


@Component
public class TweetFetcher {

    private final TwitterPullerStatusListener listener;

    @Autowired
    public TweetFetcher(TwitterPullerStatusListener listener) {
        this.listener = listener;
    }

    public TwitterStream getTwitterStream() {
        TwitterStream twitterStream = TwitterStreamFactory.getSingleton();
        twitterStream.addListener(listener);
        return twitterStream;
    }
}
