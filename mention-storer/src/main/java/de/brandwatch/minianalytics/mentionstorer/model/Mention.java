package de.brandwatch.minianalytics.mentionstorer.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

@SolrDocument
public class Mention {

    @Indexed(name = "title", type = "string")
    @Id
    private String title;

    @Indexed(name = "author", type = "string")
    private String author;

    @Indexed(name = "text", type = "string")
    private String text;

    @Indexed(name = "date", type = "string")
    private String date;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
