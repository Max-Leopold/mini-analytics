package de.brandwatch.redditscraper.reddit;

import de.brandwatch.redditscraper.kafka.Producer;
import de.brandwatch.redditscraper.model.Resource;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.IOException;
import java.time.ZonedDateTime;

public class RedditScraperForComments {

    private final Producer producer;

    @Autowired
    public RedditScraperForComments(Producer producer) {
        this.producer = producer;
    }

    public void scrapeRedditForComments(String url) throws IOException {

        Document reddit = Jsoup.connect(url).get();

        reddit.getElementsByClass("entry unvoted").stream()
                .map(this::mapToResource)
                .filter(resource -> !resource.getText().equals(""))
                .forEach(producer::send);
    }

    private Resource mapToResource(Element element) {
        Resource resource = new Resource();
        resource.setAuthor(element.select(".author").text());

        String text = element.getElementsByClass("usertext-body may-blank-within md-container ").text();
        resource.setText(text);

        String dateTime = element.getElementsByClass("live-timestamp").attr("datetime");
        ZonedDateTime zonedDateTime = ZonedDateTime.parse(dateTime);
        resource.setDate(zonedDateTime.toInstant());

        return resource;
    }
}
