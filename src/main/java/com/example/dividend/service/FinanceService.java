package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.repository.CompanyRepository;
import com.example.dividend.persist.repository.DividendRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FinanceService {
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;
    //요청이 자주 들어오는가?
    //자주 변경되는 데이터인가?
    @Cacheable(key ="#companyName" ,value = CacheKey.KEY_FINANCE)
    public ScrapedResult getDividendByCompanyName(String companyName) {
        //1. 회사명을 기준으로 회사 정보 조회
        CompanyEntity company = this.companyRepository.findByName(companyName)
                .orElseThrow(() -> new RuntimeException("Not find Company"));
        //2. 조회된 회사정보를 기준으로 배당금을 조회
        List<DividendEntity> dividends = this.dividendRepository.findAllByCompanyId(company.getId());
        //3. 결과 조합 후 반환

        List<Dividend> dividendList = dividends.stream().map(item ->
                Dividend.from(item.getDate(),item.getDividend())
        ).toList();

        return new ScrapedResult(Company.from(company.getTicker(),company.getName()),dividendList);
    }
}
