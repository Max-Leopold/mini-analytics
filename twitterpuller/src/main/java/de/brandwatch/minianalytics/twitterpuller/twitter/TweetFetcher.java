package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Ressource;
import org.springframework.beans.factory.annotation.Autowired;
import twitter4j.*;
import twitter4j.conf.ConfigurationBuilder;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;

public class TweetFetcher {

    @Autowired
    private Producer producer;


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
                Instant date = new Timestamp(status.getCreatedAt().getTime()).toInstant();

                Ressource ressource = new Ressource();
                ressource.setDate(date);
                ressource.setText(status.getText());
                ressource.setAuthor(status.getUser().getName());

                //TODO JSON Serializer und Mentions in Topic posten

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
