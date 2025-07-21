package com.example.rewrite.controller.rest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UploadPageController {
    @GetMapping("/upload")
    public String uploadPage() {
        return "upload";  // src/main/resources/templates/upload.html을 반환
    }
}