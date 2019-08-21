package de.brandwatch.minianalytics.mentiongenerator.postgres.repository;

import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRepository extends JpaRepository<Query, Long> {
}
