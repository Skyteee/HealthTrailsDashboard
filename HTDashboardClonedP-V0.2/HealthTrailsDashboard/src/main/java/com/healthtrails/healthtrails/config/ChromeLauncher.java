package com.healthtrails.healthtrails.config;

import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ChromeLauncher {

    @EventListener(ApplicationReadyEvent.class)
    public void launchChromeOnStartup() {
        String url = "http://localhost:8080/home"; // Replace with your application's URL
        String chromePath = "C:\\Program Files\\Google\\Chrome\\Application\\chrome.exe"; // Update with your Chrome path

        ProcessBuilder processBuilder = new ProcessBuilder(
                chromePath,
                "--new-window",
                "--window-size=1920,1080", // Set window size
                url
        );

        try {
            processBuilder.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

