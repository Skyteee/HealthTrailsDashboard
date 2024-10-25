package com.healthtrails.healthtrails.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/")
public class BaseURLController {

    @GetMapping("/home")
    public String index() {
        return "index";
    }

    @GetMapping("/air-data")
    public String airData() {
        return "AirQualitySearch";
    }

}
