package org.freakz.hokan_ng_springboot.bot.services.service.currency;

import org.freakz.hokan_ng_springboot.bot.common.models.GoogleCurrency;
import org.freakz.hokan_ng_springboot.bot.common.util.StaticStrings;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Petri Airio on 2.9.2015.
 */
@Service
public class GoogleCurrencyService implements CurrencyService {

    private static final Logger log = LoggerFactory.getLogger(GoogleCurrencyService.class);

    private Map<String, GoogleCurrency> currencyMap = new HashMap<>();

    @PostConstruct
    private void getCurrencies() {
        String url = "https://www.google.com/finance/converter";
        log.debug("Fetching currencies from {}", url);
        Document doc = null;
        try {
            doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
        } catch (IOException e) {
            log.error("Could not fetch currencies from {}", url);
            return;
        }
        Elements options = doc.getElementsByTag("option");
        for (Element element : options) {
            String attr = element.attr("value");
            GoogleCurrency currency = new GoogleCurrency(attr, element.text());
            currencyMap.put(attr, currency);
        }

    }

    @Override
    public List<GoogleCurrency> getGoogleCurrencies() {
        return new ArrayList<>(currencyMap.values());
    }

    @Override
    public String googleConvert(String amount, String from, String to) {
        try {
            String url = "http://www.google.com/finance/converter?a=" + amount + "&from=" + from + "&to=" + to;
            Document doc = Jsoup.connect(url).userAgent(StaticStrings.HTTP_USER_AGENT).get();
            Elements value = doc.getElementsByAttributeValue("id", "currency_converter_result");
            return value.get(0).text();
        } catch (Exception e) {
            return e.getMessage();
        }
    }
}
