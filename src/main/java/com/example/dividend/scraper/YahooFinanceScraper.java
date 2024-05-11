package com.example.dividend.scraper;

import com.example.dividend.model.Company;
import com.example.dividend.model.Dividend;
import com.example.dividend.model.ScrapedResult;
import com.example.dividend.model.constants.Month;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
@Component
public class YahooFinanceScraper implements Scraper{

    private static final String BASE_URL = "https://finance.yahoo.com/quote/%s/history?frequency=1mo&period1=%d&period2=%d";
    private static final long START_TIME = 86400; // 60*60*24
    private static final String SUMMARY_URL ="https://finance.yahoo.com/quote/%s?p=%s";
    @Override
    public ScrapedResult scrap(Company company) {
        ScrapedResult scrapedResult = new ScrapedResult();
        scrapedResult.setCompany(company);
        try {

            long now = System.currentTimeMillis() / 1000;//현제 시간

            String url = String.format(BASE_URL, company.getTicker(), START_TIME, now);
            Connection connection = Jsoup.connect(url);
            Document document = connection.get();

            Elements parsingDivs = document.getElementsByAttributeValue("class", "table svelte-ewueuo");
            Element tableEle = parsingDivs.get(0);
            ArrayList<Dividend> dividends = new ArrayList<>();
            Element tbody = tableEle.children().get(1);
            for (Element e : tbody.children()) {
                String text = e.text();
                if (!text.endsWith("Dividend")) {
                    continue;
                }

                String[] splits = text.split(" ");
                int month = Month.strToNumber(splits[0]);
                int day = Integer.parseInt(splits[1].replace(",", ""));
                int year = Integer.parseInt(splits[2]);
                String dividend = splits[3];

                if (month < 0) {
                    throw new RuntimeException("Unexpected Month enum value ->" + splits[0]);
                }

                dividends.add(
                        Dividend.from(LocalDateTime.of(year, month, day, 0, 0),dividend)
                );

            }

            scrapedResult.setDividendEntities(dividends);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }
    @Override
    public Company scrapCompanyByTicker(String ticker){
        String url = String.format(SUMMARY_URL, ticker,ticker);
        try{
            Document document = Jsoup.connect(url).get();
            Element titleEle = document.getElementsByClass("svelte-3a2v0c").get(0);
            String title = titleEle.text().split("\\(")[0].trim();
            System.out.println("title = " + title);
            return Company.from(ticker,title);
        }catch (IOException e){
            e.printStackTrace();
        }

        return null;
    }


}
