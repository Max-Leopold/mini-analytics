package de.brandwatch.minianalytics.api.postgres.repository;

import de.brandwatch.minianalytics.api.postgres.model.Query;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface QueryRepository extends JpaRepository<Query, Long> {

    List<Query> findByUserId(Long userId);
    Query findByQueryID(Long queryId);
}
