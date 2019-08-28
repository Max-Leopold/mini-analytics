package de.brandwatch.minianalytics.queryinput.postgres.repository;

import de.brandwatch.minianalytics.queryinput.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;


public interface QueryRepository extends JpaRepository<Query, Long> {
}
