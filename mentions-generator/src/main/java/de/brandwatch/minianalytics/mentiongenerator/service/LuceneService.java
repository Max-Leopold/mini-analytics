package de.brandwatch.minianalytics.mentiongenerator.service;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import de.brandwatch.minianalytics.mentiongenerator.model.Mention;
import de.brandwatch.minianalytics.mentiongenerator.model.Resource;
import de.brandwatch.minianalytics.mentiongenerator.postgres.cache.QueryCache;
import de.brandwatch.minianalytics.mentiongenerator.postgres.model.Query;
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
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;

@Service
public class LuceneService {

    private static final Logger logger = LoggerFactory.getLogger(LuceneService.class);

    private final Producer producer;

    private final QueryCache queryCache;

    @Autowired
    public LuceneService(Producer producer, QueryCache queryCache) throws IOException {
        this.producer = producer;
        this.queryCache = queryCache;
    }

    private final Directory memoryIndex = new RAMDirectory();
    private final StandardAnalyzer analyzer = new StandardAnalyzer();

    private final IndexWriterConfig indexWriterConfig = new IndexWriterConfig(analyzer);
    private final IndexWriter writer = new IndexWriter(memoryIndex, indexWriterConfig);

    private IndexSearcher searcher;
    private IndexReader reader;

    private final QueryParser queryParser = new QueryParser("text", analyzer);

    private final ConcurrentLinkedQueue<Resource> queue = new ConcurrentLinkedQueue<>();

    public void writeToQ(Resource resource) {
        queue.add(resource);
    }

    @Scheduled(fixedDelay = 5 * 1000)
    public void indexQ() throws IOException, ParseException {
        while (!queue.isEmpty()) {
            Resource resource = queue.poll();

            Document document = new Document();

            document.add(new TextField("author", resource.getAuthor(), Field.Store.YES));
            document.add(new TextField("text", resource.getText(), Field.Store.YES));
            document.add(new TextField("date", resource.getDate().toString(), Field.Store.YES));

            writer.addDocument(document);
            writer.commit();
        }

        searchIndex();
    }

    private void searchIndex() throws IOException, ParseException {

        reader = DirectoryReader.open(memoryIndex);
        searcher = new IndexSearcher(reader);

        List<Query> queries = queryCache.getCachedQueries();
        logger.info("received " + queries.size() + " queries");

        for (Query query: queries){

            org.apache.lucene.search.Query luceneQuery = queryParser.parse(query.toString());

            logger.info("Search Query: " + luceneQuery.toString());
            //The maximum of hits can only be 1 bcs. there is a maximum of 1 Document in the index
            TopDocs topDocs = searcher.search(luceneQuery, 10);

            logger.info("Total hits: " + topDocs.totalHits);

            if(topDocs.totalHits >= 1){
                Arrays.stream(topDocs.scoreDocs).forEach(x -> {
                    try {
                        Document document = searcher.doc(x.doc);

                        Mention mention = new Mention();
                        mention.setAuthor(document.get("author"));
                        mention.setText(document.get("text"));
                        mention.setQueryID(query.getQueryID());
                        mention.setDate(Instant.parse(document.get("date")));

                        logger.info("Generated Mention: " + mention);

                        producer.send(mention);
                    } catch (IOException e) {
                        logger.warn(e.getMessage());
                    }
                });
            }
        }
        writer.deleteAll();
        writer.commit();
    }
}
