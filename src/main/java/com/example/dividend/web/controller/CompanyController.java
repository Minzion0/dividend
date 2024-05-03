package com.example.dividend.web.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("/company")
@RestController
@RequiredArgsConstructor
public class CompanyController {

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){
        return null;
    }

    @GetMapping
    public ResponseEntity<?> searchCompany(){
        return null;
    }

    @PostMapping
    public ResponseEntity<?> addCompany(){
        return null;
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}
