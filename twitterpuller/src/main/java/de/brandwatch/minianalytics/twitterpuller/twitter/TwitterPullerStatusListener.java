package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;

import java.time.Instant;

@Component
public class TwitterPullerStatusListener implements StatusListener {

    private final Producer producer;

    @Autowired
    public TwitterPullerStatusListener(Producer producer) {
        this.producer = producer;
    }

    @Override
    public void onStatus(Status status) {

        Instant date = Instant.ofEpochMilli(status.getCreatedAt().getTime());

        Resource resource = new Resource();
        resource.setDate(date);
        resource.setText(status.getText());
        resource.setAuthor(status.getUser().getName());
        resource.setSourceTag("twitter");
        resource.setURL("https://twitter.com/" + status.getUser().getScreenName() + "/status/" + status.getId());

        //Post mention into Kafka Topic
        producer.send(resource);
    }

    @Override
    public void onDeletionNotice(StatusDeletionNotice statusDeletionNotice) {

    }

    @Override
    public void onTrackLimitationNotice(int i) {

    }

    @Override
    public void onScrubGeo(long l, long l1) {

    }

    @Override
    public void onStallWarning(StallWarning stallWarning) {

    }

    @Override
    public void onException(Exception e) {
        e.printStackTrace();
    }
}
