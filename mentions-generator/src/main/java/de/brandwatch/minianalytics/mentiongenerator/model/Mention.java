package de.brandwatch.minianalytics.mentiongenerator.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class Mention {

    private long queryID;

    private String author;
    private String text;

    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss", timezone = "UTC")
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
}
