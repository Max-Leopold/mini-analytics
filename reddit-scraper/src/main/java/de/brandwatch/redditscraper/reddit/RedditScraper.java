package de.brandwatch.redditscraper.reddit;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.ZonedDateTime;

public class RedditScraper {

    private static final String REDDIT_URL = "https://old.reddit.com";

    private final Producer producer;

    @Autowired
    public RedditScraper(Producer producer) {
        this.producer = producer;
    }

    public void scrapeReddit() throws IOException {
        scrapeReddit(REDDIT_URL);
    }

    public void scrapeReddit(String URL) throws IOException {
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

            producer.send(resource);
        });

        Elements nextPage = reddit.select(".next-button > a");

        nextPage.forEach(x -> {
            System.out.println(x.attr("href"));
            try {
                scrapeReddit(x.attr("href"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
