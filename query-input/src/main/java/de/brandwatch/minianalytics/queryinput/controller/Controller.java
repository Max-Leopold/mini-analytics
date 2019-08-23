package de.brandwatch.minianalytics.queryinput.controller;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@org.springframework.stereotype.Controller
public class Controller {

    Logger logger = LoggerFactory.getLogger(Controller.class);


    @RequestMapping(value = "/index")
    public String index(){
        return "index";
    }


}
