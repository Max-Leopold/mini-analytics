package de.brandwatch.minianalytics.mentionstorer.solr.repository;

import de.brandwatch.minianalytics.mentionstorer.model.Mention;
import org.springframework.data.solr.repository.SolrCrudRepository;

import java.util.List;

public interface MentionRepository extends SolrCrudRepository<Mention, String> {

    public List<Mention> findByAuthor(String author);

}
