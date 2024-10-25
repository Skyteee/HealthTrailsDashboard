package com.healthtrails.healthtrails.controllers;

import com.healthtrails.healthtrails.service.AirQualityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/air-data/")
public class AirQualityController {

    @Autowired
    private AirQualityService airQualityService;

    @Autowired
    private ObjectMapper objectMapper; // To process JSON responses

    @GetMapping("/air-quality")
    @ResponseBody // This annotation tells Spring to write the return value directly to the response body
    public ResponseEntity<Map<String, Object>> getAirQuality(@RequestParam double lat, @RequestParam double lon) {
        String response = airQualityService.getAirQuality(lat, lon);
        Map<String, Object> result = new HashMap<>();

        try {
            // Parse the JSON response
            JsonNode jsonResponse = objectMapper.readTree(response);
            JsonNode listNode = jsonResponse.path("list").get(0); // Get the first entry in the list

            // Extract the relevant data
            result.put("lat", lat);
            result.put("lon", lon);
            result.put("aqi", listNode.path("main").path("aqi").asDouble());
            result.put("co", listNode.path("components").path("co").asDouble());
            result.put("no", listNode.path("components").path("no").asDouble());
            result.put("no2", listNode.path("components").path("no2").asDouble());
            result.put("o3", listNode.path("components").path("o3").asDouble());
            result.put("so2", listNode.path("components").path("so2").asDouble());
            result.put("pm25", listNode.path("components").path("pm2_5").asDouble());
            result.put("pm10", listNode.path("components").path("pm10").asDouble());
            result.put("nh3", listNode.path("components").path("nh3").asDouble());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null); // Handle error appropriately
        }

        return ResponseEntity.ok(result); // Return the response as JSON
    }

}

