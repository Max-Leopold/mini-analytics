package de.brandwatch.minianalytics.mentiongenerator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import javax.annotation.PostConstruct;
import java.time.Instant;

@SpringBootApplication
@EnableCaching
@EnableScheduling
public class MentionsGeneratorApplication {

    public static void main(String[] args) {
        SpringApplication.run(MentionsGeneratorApplication.class, args);
    }

    @Autowired
    private Producer producer;

    @PostConstruct
    public void sendMessagesOnKafka() {
        producer.send("Waz up?");
    }

    @Bean
    public CacheManager cacheManager() {
        ConcurrentMapCacheManager cacheManager = new ConcurrentMapCacheManager("{queryID, query}");

        return cacheManager;
    }

    //Refreh Cache every 10 minutes
    @CacheEvict(allEntries = true, cacheNames = "{queryID, query}")
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void reportCacheEvict() {
        System.out.println("Flush Cache " + Instant.now());
    }
}

