package com.abreu.short_url.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController {

    @GetMapping("/")
    public static ResponseEntity<String> home() {
        return ResponseEntity.ok("Welcome to the Short URL Service!");
    }

}

