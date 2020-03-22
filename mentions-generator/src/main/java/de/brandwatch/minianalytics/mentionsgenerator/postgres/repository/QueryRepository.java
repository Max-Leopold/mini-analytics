package de.brandwatch.minianalytics.mentionsgenerator.postgres.repository;

import de.brandwatch.minianalytics.mentionsgenerator.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRepository extends JpaRepository<Query, Long> {
}
