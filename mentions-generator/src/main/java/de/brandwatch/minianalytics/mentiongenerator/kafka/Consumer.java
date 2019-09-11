package de.brandwatch.minianalytics.mentiongenerator.kafka;

import de.brandwatch.minianalytics.library.model.Resource;
import de.brandwatch.minianalytics.library.postgres.model.Query;
import de.brandwatch.minianalytics.library.postgres.repository.QueryRepository;
import de.brandwatch.minianalytics.library.solr.model.Mention;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;

import java.io.IOException;
import java.util.List;

public class Consumer {

    private static final Logger logger = LoggerFactory.getLogger(Consumer.class);

    private final QueryRepository queryRepository;

    private final Producer producer;

    private final Directory memoryIndex = new RAMDirectory();
    private final StandardAnalyzer analyzer = new StandardAnalyzer();

    private final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    private final IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);

    private IndexSearcher searcher;
    private IndexReader reader;

    private final QueryParser queryParser = new QueryParser("text", analyzer);

    @Autowired
    public Consumer(QueryRepository queryRepository, Producer producer) throws IOException {
        this.queryRepository = queryRepository;
        this.producer = producer;
    }

    @KafkaListener(topics = "resources")
    public void receive(Resource resource) throws ParseException, IOException {
        logger.info("received message='{}'", resource.toString());

        //Create Lucene Document out of Resource
        Document document = new Document();

        document.add(new TextField("author", resource.getAuthor(), Field.Store.NO));
        document.add(new TextField("text", resource.getText(), Field.Store.NO));

        logger.info("Indexed document:\n{\n\tauthor: " + document.get("author") + "\n\ttext: " + document.get("text") + "\n}");

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
                logger.info("Query " + query.getQueryID() + " hit on resource " + resource.getText());

                //Create Mention out of resource
                Mention mention = new Mention();
                mention.setQueryID(query.getQueryID());
                mention.setAuthor(resource.getAuthor());
                mention.setText(resource.getText());
                mention.setDate(resource.getDate());

                logger.info("Generated Mention: " + mention.toString());
                producer.send(mention);
            }
        }
        writer.deleteAll();
        writer.commit();
    }
}

