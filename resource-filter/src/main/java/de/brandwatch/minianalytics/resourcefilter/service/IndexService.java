package de.brandwatch.minianalytics.resourcefilter.service;

import de.brandwatch.minianalytics.resourcefilter.model.Resource;
import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrInputDocument;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;

@Service
public class IndexService {

    private final SolrClient solrClient;

    @Autowired
    public IndexService(SolrClient customSolrClient) {
        this.solrClient = customSolrClient;
    }

    public boolean isUniqueResource(Resource resource) throws IOException, SolrServerException {

        SolrQuery query = new SolrQuery();

        query.set("q", "url:\"" + resource.getURL() + "\"");
        QueryResponse response = solrClient.query(query);
        if(response.getResults().getNumFound() > 0) {
            return false;
        }

        SolrInputDocument document = new SolrInputDocument();
        document.addField("url", resource.getURL());
        document.addField("date", resource.getDate().toString());
        solrClient.add(document);
        solrClient.commit();

        return true;
    }
}
