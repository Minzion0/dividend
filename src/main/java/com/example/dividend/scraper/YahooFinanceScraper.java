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

import java.time.LocalDateTime;
import java.util.ArrayList;

public class YahooFinanceScraper {

    private static final String BASE_URL = "https://finance.yahoo.com/quote/%s/history?frequency=1mo&period1=%d&period2=%d";
    private static final long START_TIME = 86400; // 60*60*24

    public ScrapedResult scraper(Company company) {
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
                        Dividend.builder()
                                .date(LocalDateTime.of(year, month, day, 0, 0))
                                .dividend(dividend)
                                .build()
                );

            }

            scrapedResult.setDividendEntities(dividends);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return scrapedResult;
    }
    public Company scrapCompanyByTicker(String ticker){
        return null;
    }

}
