package com.example.dividend.scheduler;

import com.example.dividend.model.Company;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.CacheKey;
import com.example.dividend.persist.entity.CompanyEntity;
import com.example.dividend.persist.entity.DividendEntity;
import com.example.dividend.persist.repository.CompanyRepository;
import com.example.dividend.persist.repository.DividendRepository;
import com.example.dividend.scraper.YahooFinanceScraper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;
@Slf4j
@Component
@EnableCaching
@RequiredArgsConstructor
public class scraperScheduler {
    private final CompanyRepository companyRepository;
    private final YahooFinanceScraper yahooFinanceScraper;
    private final DividendRepository dividendRepository;



    //일정 주기마다 수행
    //코드의 유연성을 위해 yaml 에서 설정값을 받아 실행
    @CacheEvict(value = CacheKey.KEY_FINANCE,allEntries = true)
    @Scheduled(cron =" ${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        log.info("scraping scheduler is started");
        //저장된 회사 목록 조회
        List<CompanyEntity> companys = this.companyRepository.findAll();
        //회사마다 배당금 정보를 새로 스크랩핑
        for (CompanyEntity company : companys) {
            log.info("scraping scheduler is started -> {}",company.getName());
            ScrapedResult scrapedResult = this.yahooFinanceScraper.scrap(
                    Company.from(company.getTicker(),company.getName())
            );

            scrapedResult.getDividendEntities().stream()
                    //디비든 모델을 디비든 엔티티로 매핑
                    .map(e->new DividendEntity(company.getId(),e))
                   //하나씩 디비든 레파지토리에 삽입
                    .forEach(e-> {
                        boolean exists = this.dividendRepository
                                .existsByCompanyIdAndDate(e.getCompanyId(), e.getDate());
                        if (!exists){
                            this.dividendRepository.save(e);
                        }
                    });

            //연속적으로 스크랩핑 대상 사이트 서버에 요청을 날리지 않도록 일시정지
            try {
                //3초간 스레드 정지
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
                Thread.currentThread().interrupt();
            }

        }
        //스크랩핑할 배당금 정보중 데이터베이스에 없는 값은 저장
    }
}
