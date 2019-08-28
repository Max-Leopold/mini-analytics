package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import twitter4j.*;

import java.time.Instant;

public class TweetFetcher {

    public TwitterStream getTwitterStream(Producer producer) {


        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusListener() {
            public void onStatus(Status status) {
                Instant date = Instant.ofEpochMilli(status.getCreatedAt().getTime());

                Resource resource = new Resource();
                resource.setDate(date);
                resource.setText(status.getText());
                resource.setAuthor(status.getUser().getName());

                //Post mention into Kafka Topic
                producer.send(resource);

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
