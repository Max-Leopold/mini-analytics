package de.brandwatch.minianalytics.mentiongenerator.model;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.InstantSerializer;
import de.brandwatch.minianalytics.mentiongenerator.model.deserializer.DefaultInstantDeserializer;

import java.time.Instant;

public class Resource {

    private String author;
    private String text;

    @JsonSerialize(using = InstantSerializer.class)
    //Needed bcs. InstantDeserializer doesn't have a no arg. constructor
    @JsonDeserialize(using = DefaultInstantDeserializer.class)
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