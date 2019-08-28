package de.brandwatch.minianalytics.twitterpuller.postgres.repository;

import de.brandwatch.minianalytics.twitterpuller.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;

public interface QueryRepository extends JpaRepository<Query, Long> {
}
