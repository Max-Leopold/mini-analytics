package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import de.brandwatch.minianalytics.twitterpuller.postgres.cache.QueryCache;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.sql.Timestamp;
import java.time.Instant;

@Component
public class TweetSearcher {

    @Autowired
    QueryCache queryCache;

    @Autowired
    Producer producer;

    private static final Twitter twitter = TwitterFactory.getSingleton();

    @Scheduled(fixedDelay = 1000)
    public void searchTweets(){

        queryCache.getCachedQueries().forEach(query -> {
            try {
                Query searchQuery = new Query(query.getQuery());
                searchQuery.setCount(100);

                QueryResult queryResult = twitter.search(searchQuery);

                queryResult.getTweets().forEach(tweet -> {

                    Instant date = new Timestamp(tweet.getCreatedAt().getTime()).toInstant();

                    Resource resource = new Resource();
                    resource.setDate(date);
                    resource.setText(tweet.getText());
                    resource.setAuthor(tweet.getUser().getName());

                    //Post mention into Kafka Topic
                    producer.send(resource);
                });
            } catch (TwitterException e) {
                e.printStackTrace();
            }

        });
    }
}
