package com.example.cabinetmedical.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PacientController {

    @GetMapping("/")
    public String home() {
        return "redirect:/login";
    }
}