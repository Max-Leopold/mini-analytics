package de.brandwatch.redditscraper.reddit;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

public class RedditScraperForTitles {

    private static final Logger logger = LoggerFactory.getLogger(RedditScraperForTitles.class);

    private static final String REDDIT_URL = "https://old.reddit.com";

    private Set<String> visitedSites = new HashSet<>();

    private final Producer producer;

    private final RedditScraperForComments redditScraperForComments;

    @Autowired
    public RedditScraperForTitles(Producer producer, RedditScraperForComments redditScraperForComments) {
        this.producer = producer;
        this.redditScraperForComments = redditScraperForComments;
    }

    @Scheduled(fixedDelay = 1000 * 30)
    public void scrapeReddit() throws IOException {
        scrapeRedditForTitles(REDDIT_URL, 1);
    }

    public void scrapeRedditForTitles(String URL, int pageCounter) throws IOException {
        Document reddit = Jsoup.connect(URL).get();
        Elements posts = reddit.getElementsByClass("top-matter"); //Whitespace at the end is important!

        posts.forEach(x -> {

            String commentURL = x.select(".title > .title.may-blank").attr("abs:href");
            if(visitedSites.contains(commentURL)) return;
            else visitedSites.add(commentURL);

            Resource resource = new Resource();

            resource.setText(x.select(".title > .title.may-blank").text());
            resource.setAuthor(x.select(".tagline > .author").text());

            String dateTime = x.select(".tagline > .live-timestamp").attr("datetime");
            if(dateTime.equals("")) return;

            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
            resource.setDate(zonedDateTime.toInstant());

            try {
                logger.info("Scraping comments of: " + commentURL);
                redditScraperForComments.scrapeRedditForComments(commentURL);
            } catch (IOException e) {
               logger.warn(e.getMessage(), e);
            }

            producer.send(resource);
        });

        Elements nextPage = reddit.select(".next-button > a");

        for (Element element : nextPage) {
            pageCounter = pageCounter + 1;
            //Scrape top 50 sites
            if(pageCounter < 51) scrapeRedditForTitles(element.attr("href"), pageCounter);
        }
    }
}
