package de.brandwatch.minianalytics.api.postgres.model;

import javax.persistence.*;
import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Query)) return false;
        Query query1 = (Query) o;
        return query.equals(query1.query);
    }

    @Override
    public int hashCode() {
        return Objects.hash(query);
    }

    @Override
    public String toString() {
        return "{" +
                "\n\tqueryID: " + queryID +
                "\n\tquery: " + query +
                "\n}";
    }
}
