package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.*;


@Component
public class TweetFetcher {

    private TwitterPullerStatusListener listener;

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
