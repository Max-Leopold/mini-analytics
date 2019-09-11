package de.brandwatch.minianalytics.library.postgres.repository;

import de.brandwatch.minianalytics.library.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QueryRepository extends JpaRepository<Query, Long> {
}
