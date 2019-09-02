package de.brandwatch.minianalytics.api.postgres.model;

import com.google.gson.annotations.Expose;
import de.brandwatch.minianalytics.api.security.model.User;

import javax.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "queries")
public class Query {

    @Id
    @GeneratedValue
    @Column(name = "queryID")
    @Expose
    private long queryID;

    @Column(name = "query")
    @Expose
    private String query;

    @ManyToOne(targetEntity = User.class, fetch = FetchType.EAGER)
    private User user;

    public Query(String query, User user) {
        this.query = query;
        this.user = user;
    }

    public Query() {
    }

    public long getQueryID() {
        return queryID;
    }

    public void setQueryID(long queryID) {
        this.queryID = queryID;
    }

    public String getQuery() {
        return query;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
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
                "\n\tuser: " + user +
                "\n}";
    }
}
