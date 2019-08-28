package de.brandwatch.minianalytics.api.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import de.brandwatch.minianalytics.api.postgres.model.Query;
import de.brandwatch.minianalytics.api.postgres.repository.QueryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

@Component
public class QueryService {

    private static final Logger logger = LoggerFactory.getLogger(QueryService.class);

    private static final Gson gson = new GsonBuilder().setPrettyPrinting().create();

    @Autowired
    private QueryRepository queryRepository;

    public ResponseEntity createQuery(String query){
        try {
            Query saveQuery = new Query(query);

            logger.info("Retrieved query: " + query);
            return ResponseEntity.status(200).body(gson.toJson(queryRepository.save(saveQuery)));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }
    }

    public ResponseEntity getAllQueries(){
        try {
            return ResponseEntity.status(200).body(gson.toJson(queryRepository.findAll()));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }
    }

    public ResponseEntity getQueryByID(String queryID){
        try{
            return ResponseEntity.status(200).body(gson.toJson(queryRepository.findById(Long.valueOf(queryID))));
        } catch (Exception e){
            return ResponseEntity.status(500).body("Something went wrong");
        }
    }
}
