package de.brandwatch.redditscraper.reddit;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.ZonedDateTime;

public class RedditScraperForTitles {

    private static final Logger logger = LoggerFactory.getLogger(RedditScraperForTitles.class);

    private static final String REDDIT_URL = "https://old.reddit.com";

    private final Producer producer;

    private final RedditScraperForComments redditScraperForComments;

    @Autowired
    public RedditScraperForTitles(Producer producer, RedditScraperForComments redditScraperForComments) {
        this.producer = producer;
        this.redditScraperForComments = redditScraperForComments;
    }

    public void scrapeReddit() throws IOException {
        scrapeRedditForTitles(REDDIT_URL);
    }

    public void scrapeRedditForTitles(String URL) throws IOException {
        Document reddit = Jsoup.connect(URL).get();
        Elements posts = reddit.getElementsByClass("top-matter"); //Whitespace at the end is important!

        posts.forEach(x -> {
            Resource resource = new Resource();

            resource.setText(x.select(".title > .title.may-blank").text());
            resource.setAuthor(x.select(".tagline > .author").text());

            String dateTime = x.select(".tagline > .live-timestamp").attr("datetime");
            if(dateTime.equals("")) return;

            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
            resource.setDate(zonedDateTime.toInstant());

            try {
                String commentURL = x.select(".title > .title.may-blank").attr("abs:href");
                logger.info("Scraping comments of: " + commentURL);
                redditScraperForComments.scrapeRedditForComments(commentURL);
            } catch (IOException e) {
               logger.warn(e.getMessage(), e);
            }

            producer.send(resource);
        });

        Elements nextPage = reddit.select(".next-button > a");

        nextPage.forEach(x -> {
            try {
                scrapeRedditForTitles(x.attr("href"));
            } catch (IOException e) {
                logger.warn(e.getMessage(), e);
            }
        });
    }
}
