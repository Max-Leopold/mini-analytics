package de.brandwatch.minianalytics.api.security.repositories;

import de.brandwatch.minianalytics.api.security.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}
