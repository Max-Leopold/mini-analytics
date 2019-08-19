package de.brandwatch.minianalytics.mentionstorer.solr.repository;

import de.brandwatch.minianalytics.mentionstorer.model.Mention;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface MentionRepository extends SolrCrudRepository<Mention, String> {

    @Query(value = "*:*")
    List<Mention> getMentions();

}
