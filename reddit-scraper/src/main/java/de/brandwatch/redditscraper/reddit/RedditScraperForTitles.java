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

    @Scheduled(fixedDelay = 1000 * 30)
    public void scrapeReddit() throws IOException {
        scrapeRedditForTitles(REDDIT_URL, 1);
    }

    private void scrapeRedditForTitles(String url, int pageCounter) throws IOException {
        Document reddit = Jsoup.connect(url).get();

        reddit.getElementsByClass("top-matter").stream()
                .map(this::mapToResource)
                .filter(resource -> !(resource.getDate() == null))
                .forEach(producer::send);

        Elements nextPage = reddit.select(".next-button > a");

        for (Element element : nextPage) {
            pageCounter = pageCounter + 1;
            //Scrape top 50 sites
            if(pageCounter < 51) {
                scrapeRedditForTitles(element.attr("href"), pageCounter);
            }
        }
    }

    private void scrapeComments(Element element) {
        String commentURL = element.select(".title > .title.may-blank").attr("abs:href");

        try {
            logger.info("Scraping comments of: " + commentURL);
            redditScraperForComments.scrapeRedditForComments(commentURL);
        } catch (IOException e) {
            logger.warn("Error trying to scrape comments", e);
        }
    }

    private Resource mapToResource(Element element) {
        Resource resource = new Resource();

        resource.setText(element.select(".title > .title.may-blank").text());
        resource.setAuthor(element.select(".tagline > .author").text());

        String dateTime = element.select(".tagline > .live-timestamp").attr("datetime");
        if(!dateTime.equals("")) {
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
            resource.setDate(zonedDateTime.toInstant());
        } else {
            resource.setDate(null);
        }

        scrapeComments(element);

        return resource;
    }
}
