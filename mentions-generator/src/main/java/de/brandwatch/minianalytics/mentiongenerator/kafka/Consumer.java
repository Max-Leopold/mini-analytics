package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.mentiongenerator.model.Mention;
import de.brandwatch.minianalytics.mentiongenerator.model.Ressource;
import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
import de.brandwatch.minianalytics.mentiongenerator.postgres.repository.QueryRepository;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.util.List;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    @Autowired
    QueryRepository queryRepository;

    @Autowired
    Producer producer;

    Directory memoryIndex = new RAMDirectory();
    StandardAnalyzer analyzer = new StandardAnalyzer();

    IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);

    IndexSearcher searcher;
    IndexReader reader;

    QueryParser queryParser = new QueryParser("text", analyzer);

    public Consumer() throws IOException {
    }


    @KafkaListener(topics = "twitter")
    public void receive(Ressource ressource) throws ParseException, IOException {
        logger.info("received message='{}'", ressource.toString());

        //TODO Create Lucene Document out of Ressource
        Document document = new Document();

        document.add(new TextField("author", ressource.getAuthor(), Field.Store.YES));
        document.add(new TextField("text", ressource.getText(), Field.Store.YES));

        logger.info("Document text: " + document.get("text"));
        logger.info("Document author: " + document.get("author"));

        writer.addDocument(document);
        writer.commit();

        reader = DirectoryReader.open(memoryIndex);
        searcher = new IndexSearcher(reader);


        //TODO Fetch all Queries from Database -> Cache Queries
        List<Query> queries = queryRepository.findAll();
        logger.info("received " + queries.size() + " queries");


        for (Query query : queries) {

            org.apache.lucene.search.Query luceneQuery = queryParser.parse(query.toString());

            logger.info("Search Query: " + luceneQuery.toString());

            //The maximum of hits can only be 1 bcs. there is a maximum of 1 Document in the index
            TopDocs topDocs = searcher.search(luceneQuery, 10);

            logger.info("Total hits: " + topDocs.totalHits);

            if (topDocs.totalHits == 1) {

                logger.info("Query " + query.getQueryID() + " hit on ressource " + ressource.getText());

                //Create Mention out of ressource
                Mention mention = new Mention();

                mention.setQueryID(query.getQueryID());
                mention.setAuthor(ressource.getAuthor());
                mention.setText(ressource.getText());
                mention.setDate(ressource.getDate());

                logger.info("Generated Mention: " + mention.toString());

                producer.send(mention);

            }
        }

        writer.deleteAll();
        writer.commit();


    }
}

