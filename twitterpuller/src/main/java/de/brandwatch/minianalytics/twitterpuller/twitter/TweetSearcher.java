package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import de.brandwatch.minianalytics.twitterpuller.postgres.cache.QueryCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import twitter4j.*;

import java.time.Instant;

@Component
public class TweetSearcher {

    private static final Logger logger = LoggerFactory.getLogger(TweetSearcher.class);

    private final QueryCache queryCache;

    private final Producer producer;

    @Autowired
    public TweetSearcher(QueryCache queryCache, Producer producer) {
        this.queryCache = queryCache;
        this.producer = producer;
    }

    private static final Twitter twitter = TwitterFactory.getSingleton();

    @Scheduled(fixedDelay = 1000)
    public void searchTweets(){
        queryCache.getCachedQueries().forEach(this::search);
    }

    private void search(de.brandwatch.minianalytics.twitterpuller.postgres.model.Query query){
        try {
            Query searchQuery = new Query(query.getQuery());
            searchQuery.setCount(100);

            QueryResult queryResult = twitter.search(searchQuery);

            queryResult.getTweets().stream()
                    .map(this::mapToResource)
                    .forEach(producer::send);

        } catch (TwitterException e) {
            logger.warn(e.getErrorMessage());
        }
    }

    private Resource mapToResource(Status tweet) {
        Instant date = Instant.ofEpochMilli(tweet.getCreatedAt().getTime());

        Resource resource = new Resource();
        resource.setDate(date);
        resource.setText(tweet.getText());
        resource.setAuthor(tweet.getUser().getName());
        return resource;
    }
}
