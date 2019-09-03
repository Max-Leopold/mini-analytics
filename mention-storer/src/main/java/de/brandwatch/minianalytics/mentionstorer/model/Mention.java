package de.brandwatch.minianalytics.mentionstorer.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import org.springframework.data.annotation.Id;
import org.springframework.data.solr.core.mapping.Indexed;
import org.springframework.data.solr.core.mapping.SolrDocument;

import java.time.Instant;

@SolrDocument(collection = "mentions")
public class Mention {

    @Id
    @Indexed
    private String id;

    @Indexed(name = "queryID", type = "long")
    private long queryID;

    @Indexed(name = "author", type = "string")
    private String author;

    @Indexed(name = "text", type = "string")
    private String text;

    @Indexed(name = "date", type = "date")
    @JsonDeserialize(using = InstantDeserializer.class)
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
}
