package com.healthtrails.healthtrails.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

@Service
public class WeatherService {
    private final String apiKey = "36b5e956f911b948cf7d0757bbdd5611";
    private final String weatherUrl = "http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&appid={apiKey}";

    @Autowired
    private RestTemplate restTemplate;

    public String getWeather(double lat, double lon) {
        HashMap<String, String> params = new HashMap<>();
        params.put("lat", String.valueOf(lat));
        params.put("lon", String.valueOf(lon));
        params.put("apiKey", apiKey);
        ResponseEntity<String> response = restTemplate.getForEntity(weatherUrl, String.class, params);
        System.out.println(response.getBody());
        return response.getBody();
    }


}
