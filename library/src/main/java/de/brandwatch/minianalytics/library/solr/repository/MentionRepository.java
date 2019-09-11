package de.brandwatch.minianalytics.library.solr.repository;

import de.brandwatch.minianalytics.library.solr.model.Mention;
import org.springframework.data.solr.repository.Query;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface MentionRepository extends SolrCrudRepository<Mention, String> {

    @Query("*:*")
    List<Mention> findMentions();

    List<Mention> findByQueryID(Long QueryID);

    @Query("queryID:?0 AND date:?1")
    List<Mention> findMentionsAfterDate(Long QueryID, String date);
}
