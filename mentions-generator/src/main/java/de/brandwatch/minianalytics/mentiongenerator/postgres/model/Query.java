package de.brandwatch.minianalytics.mentiongenerator.postgres.model;

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
}
