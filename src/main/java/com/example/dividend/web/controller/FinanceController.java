package com.example.dividend.web.controller;

import com.example.dividend.model.ScrapedResult;
import com.example.dividend.service.FinanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
@RequestMapping("/finance")
@RestController
@RequiredArgsConstructor
public class FinanceController {
    private final FinanceService financeService;
    @GetMapping("/dividend/{companyName}")
    public ResponseEntity<?> searchFinance(@PathVariable String companyName){
        ScrapedResult scrapedResult = financeService.getDividendByCompanyName(companyName);
        return ResponseEntity.ok(scrapedResult);
    }
}
