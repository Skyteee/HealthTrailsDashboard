package com.healthtrails.healthtrails.controllers;


import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;

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

    @GetMapping("/data-sources")
    public String dataSources(Model model) {
        try {
            // Use ClassPathResource to get the path to the "files" folder
            Path folderPath = new ClassPathResource("static/files").getFile().toPath();

            // List all PDF files in the folder and map to a list of filenames
            List<String> pdfFiles = Files.list(folderPath)
                    .filter(Files::isRegularFile)
                    .filter(path -> path.toString().endsWith(".pdf"))
                    .map(Path::getFileName)
                    .map(Path::toString)
                    .collect(Collectors.toList());

            model.addAttribute("pdfFiles", pdfFiles);

        } catch (IOException e) {
            e.printStackTrace();
            model.addAttribute("error", "Could not load PDF files.");
        }

        return "Data-Sources";
    }

    @GetMapping("/flora-fauna")
    public String floraFauna() {
        return "flora-fauna";
    }

}
