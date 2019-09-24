package de.brandwatch.minianalytics.api.service;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.api.security.model.User;
import de.brandwatch.minianalytics.api.security.repositories.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

@Component
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    private final QueryRepository queryRepository;

    private final UserRepository userRepository;

    @Autowired
    public QueryService(QueryRepository queryRepository, UserRepository userRepository) {
        this.queryRepository = queryRepository;
        this.userRepository = userRepository;
    }

    public Query createQuery(String query) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUsername(username);
        Query saveQuery = new Query(query, user);

        logger.info("Retrieved query: " + query);
        return queryRepository.save(saveQuery);
    }

    public List<Query> getAllQueries() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUsername(username);
        Long userId = user.getId();

        return queryRepository.findByUserId(userId);
    }

    public Optional<Query> getQueryByID(String queryID) throws Exception {

        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();

        User user = userRepository.findByUsername(username);
        Long userId = user.getId();

        Optional<Query> query = queryRepository.findById(Long.valueOf(queryID));

        if(query.isPresent() && query.get().getUser().getId().equals(userId)) {
            return queryRepository.findById(Long.valueOf(queryID));
        }

        throw new Exception("Query " + queryID + " doesnt belong to user " + username);
    }
}
