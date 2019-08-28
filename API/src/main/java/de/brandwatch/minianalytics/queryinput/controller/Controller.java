package de.brandwatch.minianalytics.queryinput.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;

@org.springframework.stereotype.Controller
public class Controller {

    private static final Logger logger = LoggerFactory.getLogger(Controller.class);


    @GetMapping(value = "/index")
    public String index(){
        return "index";
    }


}
