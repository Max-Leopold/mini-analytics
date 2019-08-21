package de.brandwatch.minianalytics.twitterpuller.model;

import java.time.Instant;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantTimeSerializer;

import java.time.Instant;

public class Resource {

    private String author;
    private String text;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantTimeDeserializer.class)
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
}
