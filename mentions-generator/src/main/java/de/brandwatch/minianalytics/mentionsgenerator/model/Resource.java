package de.brandwatch.minianalytics.mentionsgenerator.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;

public class Resource {

    private String author;
    private String text;

    @JsonFormat(pattern = "yyy-MM-dd HH:mm:ss", timezone = "UTC")
    private Instant date;

    public Resource() {
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

    @Override
    public String toString() {
        return "{\n" +
                "\tauthor: " + getAuthor() + "\n" +
                "\ttext: " + getText() + "\n" +
                "\tdate: " + getDate().toString() + "\n" +
                "}";
    }
}
