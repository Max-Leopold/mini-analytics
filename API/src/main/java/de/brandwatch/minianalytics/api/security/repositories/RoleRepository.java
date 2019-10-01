package de.brandwatch.minianalytics.api.security.repositories;

import de.brandwatch.minianalytics.api.security.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
}
