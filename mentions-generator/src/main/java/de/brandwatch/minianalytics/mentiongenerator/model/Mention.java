package de.brandwatch.minianalytics.mentiongenerator.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.deser.InstantDeserializer;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;

import java.time.Instant;
import java.util.Objects;

public class Mention {

    private long queryID;

    private String author;
    private String text;

    @JsonSerialize(using = InstantSerializer.class)
    @JsonDeserialize(using = InstantDeserializer.class)
    private Instant date;

    public Mention() {
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

    public Instant getDate() {
        return date;
    }

    public void setDate(Instant date) {
        this.date = date;
    }

    public long getQueryID() {
        return queryID;
    }

    public void setQueryID(long queryID) {
        this.queryID = queryID;
    }

    @Override
    public String toString() {
        return "{\n" +
                "\tqueryID: " + getQueryID() + "\n" +
                "\tauthor: " + getAuthor() + "\n" +
                "\ttext: " + getText() + "\n" +
                "\tdate: " + getDate().toString() + "\n" +
                "}";
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
}
