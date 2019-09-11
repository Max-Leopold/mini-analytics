package de.brandwatch.minianalytics.mentiongenerator;

import de.brandwatch.minianalytics.mentiongenerator.kafka.Producer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;

@SpringBootApplication
@EnableCaching
@EnableScheduling
@EntityScan(basePackages = {"de.brandwatch.minianalytics.library.postgres.model"})
@EnableJpaRepositories(basePackages = {"de.brandwatch.minianalytics.library.postgres.repository"})
public class MentionsGeneratorApplication {

    private static final Logger logger = LoggerFactory.getLogger(MentionsGeneratorApplication.class);

    public static void main(String[] args) {
        SpringApplication.run(MentionsGeneratorApplication.class, args);
    }

    private Producer producer;

    @Autowired
    public MentionsGeneratorApplication(Producer producer){
        this.producer = producer;
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("{queryID, query}");
    }

    //Refreh Cache every 10 minutes
    @CacheEvict(allEntries = true, cacheNames = "{queryID, query}")
    @Scheduled(fixedDelay = 10 * 60 * 1000)
    public void reportCacheEvict() {
        logger.info("Flush Cache " + Instant.now());
    }
}

