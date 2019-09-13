package de.brandwatch.minianalytics.api.solr.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.google.gson.annotations.Expose;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.time.Instant;
import java.util.Objects;

@SolrDocument(collection = "mentions")
public class Mention {

    @Id
    @Indexed
    private String id;

    @Indexed(name = "queryID", type = "long")
    @Expose
    private long queryID;

    @Indexed(name = "author", type = "string")
    @Expose
    private String author;

    @Indexed(name = "text", type = "string")
    @Expose
    private String text;

    @Indexed(name = "date", type = "date")
    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant date;

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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public long getQueryID() {
        return queryID;
    }

    public void setQueryID(long queryID) {
        this.queryID = queryID;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Mention)) return false;
        Mention mention = (Mention) o;
        return getQueryID() == mention.getQueryID() &&
                getAuthor().equals(mention.getAuthor()) &&
                getText().equals(mention.getText()) &&
                getDate().equals(mention.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getQueryID(), getAuthor(), getText(), getDate());
    }

    @Override
    public String toString() {
        return "Mention{" +
                "id='" + id + '\'' +
                ", queryID=" + queryID +
                ", author='" + author + '\'' +
                ", text='" + text + '\'' +
                ", date=" + date +
                '}';
    }
}
