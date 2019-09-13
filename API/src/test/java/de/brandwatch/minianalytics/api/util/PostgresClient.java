package de.brandwatch.minianalytics.api.util;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.security.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class PostgresClient {

    private static final Logger logger = LoggerFactory.getLogger(PostgresClient.class);

    private final Connection connection;

    public PostgresClient(String jdbcAddress, String user, String password) throws SQLException {
        connection = DriverManager.getConnection(jdbcAddress, user, password);
        logger.info("Successfully connected to database");

        initDB();
    }

    private void initDB() throws SQLException {
        Statement statement = connection.createStatement();

        //Query Table
        statement.execute("create table queries (queryid int8 not null, query varchar(255), user_id int8, primary key (queryid))");
        //Role Table
        statement.execute("create table role (id int8 not null, name varchar(255), primary key (id))");
        //User Table
        statement.execute("create table user_table (id int8 not null, password varchar(255), username varchar(255), primary key (id))");
        //User Role Table
        statement.execute("create table user_table_roles (user_id int8 not null, roles_id int8 not null, primary key (user_id, roles_id))");
        //Create Hibernate Sequence
        statement.execute("create sequence hibernate_sequence start 1 increment 1");
        //Query User
        statement.execute("alter table if exists queries add constraint FK1bsbdi8ysk7c93vxrntt9qy13 foreign key (user_id) references user_table");
        //User Role
        statement.execute("alter table if exists user_table_roles add constraint FKkr1sfrdm6p2maklrdfg8vma1s foreign key (roles_id) references role");
        //User Role
        statement.execute("alter table if exists user_table_roles add constraint FKhn8b43sudbfyi60j8m0he2num foreign key (user_id) references user_table");
    }

    public void inserUser(User user) throws SQLException {
        connection.createStatement().execute("insert into user_table (password, username, id) values ('" +
                user.getPassword() + "', '" +
                user.getUsername() + "', " +
                user.getId() + ")");
    }

    public void insertQuery(Query query) throws SQLException {
        connection.createStatement().execute("insert into queries (query, user_id, queryid) values ('" +
                query.getQuery() + "', " +
                query.getUser().getId() + ", " +
                query.getQueryID() + ")");
    }

    public void deleteAll() throws SQLException {
        connection.createStatement().execute("delete from queries");
        connection.createStatement().execute("delete from user_table");
    }

    public User findUserByUserId(long id) throws SQLException {
        User user = new User();

        ResultSet resultSet = connection.createStatement().executeQuery("select * from user_table where id = '" + id + "'");
        while (resultSet.next()){

            user.setId((long) resultSet.getInt("id"));
            user.setRoles(new HashSet<>());
            user.setUsername(resultSet.getString("username"));
            user.setPassword(resultSet.getString("password"));
        }

        return user;
    }

    public List<Query> findAllQueries() throws SQLException {
        List<Query> queries = new ArrayList<>();

        ResultSet resultSet = connection.createStatement().executeQuery("select * from queries");
        while (resultSet.next()){
            Query query = new Query();

            query.setQueryID(resultSet.getInt("queryid"));
            query.setQuery(resultSet.getString("query"));
            query.setUser(findUserByUserId(resultSet.getInt("user_id")));
            queries.add(query);
        }

        return queries;
    }
}
