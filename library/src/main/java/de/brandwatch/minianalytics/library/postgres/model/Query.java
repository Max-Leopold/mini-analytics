package de.brandwatch.minianalytics.library.postgres.model;

import javax.persistence.*;

@Entity
@Table(name = "queries")
public class Query {

    @Id
    @GeneratedValue
    @Column(name = "queryID")
    private long queryID;

    @Column(name = "query")
    private String query;

    public Query(String query) {
        this.query = query;
    }

    public Query() {
    }

    public long getQueryID() {
        return queryID;
    }

    public String getQuery() {
        return query;
    }

    @Override
    public String toString() {
        return "{" +
                "\n\tqueryID: " + queryID +
                "\n\tquery: " + query +
                "\n}";
    }
}
