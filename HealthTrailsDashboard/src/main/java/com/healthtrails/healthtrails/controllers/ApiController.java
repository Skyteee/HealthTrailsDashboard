package com.healthtrails.healthtrails.controllers;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.healthtrails.healthtrails.service.AirQualityService;
import com.healthtrails.healthtrails.service.WeatherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Controller
@RequestMapping("/")
public class ApiController {

    @Autowired
    private AirQualityService airQualityService;

    @Autowired
    private WeatherService weatherService;

    @Autowired
    private ObjectMapper objectMapper; // To process JSON responses

    @GetMapping("/air-data/air-quality")
    @ResponseBody
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

    @GetMapping("/weather-data/weather")
    @ResponseBody
    public ResponseEntity<Map<String, Object>> getWeather(@RequestParam double lat, @RequestParam double lon) {
        String response = weatherService.getWeather(lat, lon);
        Map<String, Object> result = new HashMap<>();

        try {
            JsonNode jsonResponse = objectMapper.readTree(response);

            // Extract coordinates
            JsonNode coordNode = jsonResponse.path("coord");
            result.put("lon", coordNode.path("lon").asDouble());
            result.put("lat", coordNode.path("lat").asDouble());

            // Extract weather information
            JsonNode weatherNode = jsonResponse.path("weather").get(0);
            result.put("main", weatherNode.path("main").asText());
            result.put("description", weatherNode.path("description").asText());
            result.put("icon", weatherNode.path("icon").asText());

            // Extract main weather details
            JsonNode mainNode = jsonResponse.path("main");
            result.put("temp", mainNode.path("temp").asDouble());
            result.put("feels_like", mainNode.path("feels_like").asDouble());
            result.put("temp_min", mainNode.path("temp_min").asDouble());
            result.put("temp_max", mainNode.path("temp_max").asDouble());
            result.put("pressure", mainNode.path("pressure").asInt());
            result.put("humidity", mainNode.path("humidity").asInt());
            result.put("sea_level", mainNode.path("sea_level").asInt());
            result.put("grnd_level", mainNode.path("grnd_level").asInt());

            // Extract visibility
            result.put("visibility", jsonResponse.path("visibility").asInt());

            // Extract wind information
            JsonNode windNode = jsonResponse.path("wind");
            result.put("wind_speed", windNode.path("speed").asDouble());
            result.put("wind_deg", windNode.path("deg").asInt());
            result.put("wind_gust", windNode.path("gust").asDouble());

            // Extract cloud information
            JsonNode cloudsNode = jsonResponse.path("clouds");
            result.put("clouds_all", cloudsNode.path("all").asInt());

            // Extract system information
            JsonNode sysNode = jsonResponse.path("sys");
            result.put("country", sysNode.path("country").asText());
            long sunriseUnix = jsonResponse.path("sys").path("sunrise").asLong();
            long sunsetUnix = jsonResponse.path("sys").path("sunset").asLong();
            result.put("sunrise", convertUnixToDateTime(sunriseUnix));
            result.put("sunset", convertUnixToDateTime(sunsetUnix));

            // Additional information
            result.put("timezone", jsonResponse.path("timezone").asInt());
            result.put("id", jsonResponse.path("id").asInt());
            result.put("name", jsonResponse.path("name").asText());
            result.put("cod", jsonResponse.path("cod").asInt());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }

        return ResponseEntity.ok(result);
    }

    private String convertUnixToDateTime(long unixTimestamp) {
        Instant instant = Instant.ofEpochSecond(unixTimestamp);
        LocalDateTime dateTime = LocalDateTime.ofInstant(instant, ZoneId.systemDefault());
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // Customize format as needed
        return dateTime.format(formatter);
    }

    @ExceptionHandler(HttpClientErrorException.class)
    public String handleHttpClientError(HttpClientErrorException e) {
        System.out.print("Error Handled!");
        return "redirect:/404"; // Redirect to an error page or the same page
    }

}
