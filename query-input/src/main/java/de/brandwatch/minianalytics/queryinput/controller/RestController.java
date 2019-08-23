package de.brandwatch.minianalytics.queryinput.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.web.bind.annotation.RestController
public class RestController {

    Logger logger = LoggerFactory.getLogger(RestController.class);

    @RequestMapping(value = "/send")
    public void send(@RequestParam String name){

        //TODO parse Query into Lucene Query
        logger.info("Retrieved name: " + name);
    }
}
