package de.brandwatch.minianalytics.api.postgres.repository;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QueryRepository extends JpaRepository<Query, Long> {
}
