package de.brandwatch.minianalytics.twitterpuller.twitter;

import org.springframework.stereotype.Service;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

@Service
public class TweetFetcher {

    public TwitterStream getTwitterStream() {

        ConfigurationBuilder cb = new ConfigurationBuilder();
        cb.setDebugEnabled(true)
                .setOAuthConsumerKey("wGBh0b0UGX8jQTKiBxZ3Mzzl8")
                .setOAuthConsumerSecret("oIWcVidvgHfJc7j1xwpcURAAEu1CJ8sSLRlDWvqfDr3eEKr0BL")
                .setOAuthAccessToken("1163430424233877505-5noGEVbFdSMIHsCRyjDnUQ1ntohqoa")
                .setOAuthAccessTokenSecret("qkekLeJbxK7sv9BXF8D9Xv30FZ1jhsitF9RTY2PtsRt7f");

        TwitterStream twitterStream = new TwitterStreamFactory(cb.build()).getInstance();
        twitterStream.addListener(new StatusListener() {
            public void onStatus(Status status) {
                System.out.println(status.getText());
            }

            public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {
            }

            public void onTrackLimitationNotice(int i) {

            }

            public void onScrubGeo(long l, long l1) {

            }

            public void onStallWarning(StallWarning stallWarning) {

            }

            public void onException(Exception e) {
                e.printStackTrace();
            }
        });

        return twitterStream;
    }
}
