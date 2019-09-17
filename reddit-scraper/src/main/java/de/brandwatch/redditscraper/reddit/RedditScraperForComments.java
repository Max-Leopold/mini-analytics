package de.brandwatch.redditscraper.reddit;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.ZonedDateTime;

public class RedditScraperForComments {

    private final Producer producer;

    @Autowired
    public RedditScraperForComments(Producer producer) {
        this.producer = producer;
    }

    public void scrapeRedditForComments(String URL) throws IOException {

        Document reddit = Jsoup.connect(URL).get();

        Elements elements = reddit.getElementsByClass("entry unvoted");

        elements.forEach(x -> {
            Resource resource = new Resource();
            resource.setAuthor(x.select(".author").text());

            String text = x.getElementsByClass("usertext-body may-blank-within md-container ").text();
            if(text.equals("")) return;
            resource.setText(text);

            String dateTime = x.getElementsByClass("live-timestamp").attr("datetime");
            ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
            resource.setDate(zonedDateTime.toInstant());

            producer.send(resource);
        });
    }
}
