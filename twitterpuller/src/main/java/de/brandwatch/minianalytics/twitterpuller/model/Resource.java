package de.brandwatch.minianalytics.twitterpuller.model;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.time.Instant;
import java.util.Objects;

public class Resource {

    private String author;
    private String text;

    @JsonFormat
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Resource)) return false;
        Resource resource = (Resource) o;
        return Objects.equals(getAuthor(), resource.getAuthor()) &&
                Objects.equals(getText(), resource.getText()) &&
                Objects.equals(getDate(), resource.getDate());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getAuthor(), getText(), getDate());
    }
}
