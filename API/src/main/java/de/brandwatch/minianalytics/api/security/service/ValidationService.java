package de.brandwatch.minianalytics.api.security.service;

import com.google.common.base.Preconditions;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.api.security.model.User;
import de.brandwatch.minianalytics.api.security.repositories.UserRepository;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
public class ValidationService {

    private final UserRepository userRepository;

    private final QueryRepository queryRepository;

    public ValidationService(UserRepository userRepository, QueryRepository queryRepository) {
        this.userRepository = userRepository;
        this.queryRepository = queryRepository;
    }

    public void validateUser(String queryID) {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = ((UserDetails) principal).getUsername();
        User user = userRepository.findByUsername(username);
        Preconditions.checkArgument(queryRepository.findById(Long.valueOf(queryID)).get().getUser().equals(user),
                "Query " + queryID + "doesn't belong to user " + username);
    }
}
