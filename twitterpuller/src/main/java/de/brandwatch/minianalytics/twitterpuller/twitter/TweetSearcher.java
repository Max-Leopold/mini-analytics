package de.brandwatch.minianalytics.twitterpuller.twitter;

import de.brandwatch.minianalytics.twitterpuller.kafka.Producer;
import de.brandwatch.minianalytics.twitterpuller.model.Resource;
import de.brandwatch.minianalytics.twitterpuller.postgres.cache.QueryCache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import twitter4j.*;

import java.time.Instant;

//@Component
public class TweetSearcher {

    private static final Logger logger = LoggerFactory.getLogger(TweetSearcher.class);

    private final QueryCache queryCache;

    private final Producer producer;

    private final Twitter twitter;

    //@Autowired
    public TweetSearcher(QueryCache queryCache, Producer producer, Twitter twitter) {
        this.queryCache = queryCache;
        this.producer = producer;
        this.twitter = twitter;
    }


    //@Scheduled(fixedDelay = 1000)
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
            logger.error(e.getErrorMessage());
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
