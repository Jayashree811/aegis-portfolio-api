package com.portfolio.api.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UIController {

    @GetMapping("/")
    public String index() {
        return "index"; // serve src/main/resources/templates/index.html
    }
}
