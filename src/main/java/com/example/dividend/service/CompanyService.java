package com.example.dividend.service;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.repository.CompanyRepository;
import com.example.dividend.persist.repository.DividendRepository;
import com.example.dividend.scraper.Scraper;
import lombok.RequiredArgsConstructor;
import org.apache.commons.collections4.Trie;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class CompanyService {

    private final Trie trie;
    private final Scraper YahooFinanceScraper;
    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    public Company save(String ticker){
        boolean exists = this.companyRepository.existsByTicker(ticker);
        if (exists){
            throw new RuntimeException("already exists ticker");
        }
        return this.storeCompanyAndDividend(ticker);
    }
    public Page<CompanyEntity> getAllCompany(Pageable pageable){
       return this.companyRepository.findAll(pageable);
    }
    private Company storeCompanyAndDividend(String ticker){
        //ticker 를 기준으로 회사를 스크래핑
        Company company = this.YahooFinanceScraper.scrapCompanyByTicker(ticker);
        if (ObjectUtils.isEmpty(company)){
            throw new RuntimeException("failed to scarp ticker ->"+ticker);
        }
        //해당 회사가 존재할 경우, 회사 배당금 정보를 스크래핑
        ScrapedResult scrapedResult = this.YahooFinanceScraper.scrap(company);

        //스크래핑 결과
        CompanyEntity companyEntity = this.companyRepository.save(new CompanyEntity(company));
        List<DividendEntity> dividendEntities = scrapedResult.getDividendEntities().stream()
                .map(dividend -> new DividendEntity(companyEntity.getId(), dividend))
                .toList();
        this.dividendRepository.saveAll(dividendEntities);

        return company;
    }

    public void addAutocompleteKeyword(String keyword){
        this.trie.put(keyword,null);
    }

    public List<String> autocomplete(String keyword){

        return (List<String>) this.trie.prefixMap(keyword).keySet()
               .stream()
               .collect(Collectors.toList());
    }

    public List<String> getCompanyNameByKeyword(String keyword){
        PageRequest limit = PageRequest.of(0, 10);
        return this.companyRepository.findByNameStartingWithIgnoreCase(keyword,limit).stream()
                .map(CompanyEntity::getName).toList();
    }

    public void deleteAutocompleteKeyword(String keyword){
        this.trie.remove(keyword);
    }
}
