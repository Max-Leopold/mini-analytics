package de.brandwatch.minianalytics.queryinput.solr.repository;

import de.brandwatch.minianalytics.queryinput.solr.model.Mention;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface MentionRepository extends SolrCrudRepository<Mention, String> {

    List<Mention> findByQueryID(long queryID);
}
