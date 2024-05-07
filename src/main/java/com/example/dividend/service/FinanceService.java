package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.repository.CompanyRepository;
import com.example.dividend.persist.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    public ScrapedResult getDividendByCompanyName(String companyName){
        //1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("Not find Company"));
        //2. 조회된 회사정보를 기준으로 배당금을 조회
        List<DividendEntity> dividends = this.dividendRepository.findAllByCompanyId(company.getId());
        //3. 결과 조합 후 반환

        List<Dividend> dividendList = dividends.stream().map(item ->
                Dividend.builder()
                        .dividend(item.getDividend())
                        .date(item.getDate())
                        .build()
        ).toList();

        return new ScrapedResult(Company.builder()
                .ticker(company.getTicker())
                .name(company.getName())
                .build(),dividendList);
    }
}
