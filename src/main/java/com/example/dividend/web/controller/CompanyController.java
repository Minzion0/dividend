package com.example.dividend.web.controller;

import com.example.dividend.model.Company;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.service.CompanyService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/company")
@RestController
@RequiredArgsConstructor
public class CompanyController {

    private final CompanyService companyService;

    @GetMapping("/autocomplete")
    public ResponseEntity<?> autocomplete(@RequestParam String keyword){
        List<String> autocomplete = companyService.getCompanyNameByKeyword(keyword);
        return ResponseEntity.ok(autocomplete);
    }

    @GetMapping
    @PreAuthorize("hasRole('READ')")
    public ResponseEntity<?> searchCompany(final Pageable pageable){
        Page<CompanyEntity> companies = companyService.getAllCompany(pageable);
        return ResponseEntity.ok(companies);
    }

    /**
     * 회사 및 배당금 정보 추가
     * @param request
     * @return
     */
    @PostMapping
    @PreAuthorize("hasRole('WRITE')")
    public ResponseEntity<?> addCompany(@RequestBody Company request){
        String ticker = request.getTicker().trim();
        if (ObjectUtils.isEmpty(ticker)){
            throw new RuntimeException("ticker is empty");
        }
        Company company = this.companyService.save(ticker);
        this.companyService.addAutocompleteKeyword(company.getName());
        return ResponseEntity.ok(company);
    }

    @DeleteMapping
    public ResponseEntity<?> deleteCompany(){
        return null;
    }
}
