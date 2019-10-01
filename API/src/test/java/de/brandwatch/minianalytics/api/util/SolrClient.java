package de.brandwatch.minianalytics.api.util;

import de.brandwatch.minianalytics.api.solr.model.Mention;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;

public class SolrClient {

    private final org.apache.solr.client.solrj.SolrClient solrClient;

    public SolrClient(String solrUrl) {
        this.solrClient = new HttpSolrClient.Builder(solrUrl).build();
    }

    public void save(Mention mention) throws IOException, SolrServerException {
        SolrInputDocument document = new SolrInputDocument();
        document.addField("queryID", mention.getQueryID());
        document.addField("date", mention.getDate().toString());
        document.addField("text", mention.getText());
        document.addField("author", mention.getAuthor());
        document.addField("id", 1);

        solrClient.add("mentions", document);
        solrClient.commit("mentions");
    }
}
