package de.brandwatch.minianalytics;

import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.impl.XMLResponseParser;
import org.apache.solr.common.SolrInputDocument;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) throws IOException, SolrServerException {

        String urlString = "http://localhost:8983/solr/mentions_core";
        HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();
        solr.setParser(new XMLResponseParser());

        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        LocalDate localDate = LocalDate.now();
        System.out.println(dtf.format(localDate)); //2016/11/16

        SolrInputDocument document = new SolrInputDocument();

        document.addField("date", dtf.format(localDate));
        document.addField("author", "Max Leopold");
        document.addField("title", "Hello World");
        document.addField("text", "Hello World");

        solr.add(document);
        solr.commit();
    }
}
