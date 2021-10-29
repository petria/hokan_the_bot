package org.freakz.hokan_ng_springboot.bot.common.service.translate;

import org.freakz.hokan_ng_springboot.bot.common.models.TranslateData;
import org.freakz.hokan_ng_springboot.bot.common.models.TranslateResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio on 12.11.2015.
 * -
 */
@Service
public class SanakirjaOrgTranslateServiceImpl implements SanakirjaOrgTranslateService {

    private static final Logger log = LoggerFactory.getLogger(SanakirjaOrgTranslateServiceImpl.class);

    private List<TranslateData> getTranslations(Elements elements) {
        List<TranslateData> results = new ArrayList<>();
        for (int i = 0; i < elements.size(); i++) {
            Element t = elements.get(i);
            String number = t.child(0).text();
            if (number.endsWith(".")) {
                String translation = t.child(1).text();
                String context = "";
/*      TODO: FIX !!!
       if (t.child(2) != null) {
          context = t.child(2).text();
        }*/
                if (translation != null && translation.length() > 0) {
                    results.add(new TranslateData(translation, context));
                }
            }
        }
        return results;
    }

    private List<TranslateData> getTranslationsFromUrl(String url) {
        try {
            Document doc = Jsoup.connect(url).get();
            Elements row1 = doc.getElementsByClass("sk-row1");
            Elements row2 = doc.getElementsByClass("sk-row2");
            List<TranslateData> results = getTranslations(row1);
            results.addAll(getTranslations(row2));
            return results;
        } catch (Exception e) {
            log.error("Translation fetch failed: {}", url, e);
        }
        return new ArrayList<>();

    }

    @Override
    public List<TranslateData> translateFiEng(String keyword) {
        String url = "http://www.sanakirja.org/search.php?l=17&l2=3&dont_switch_languages&q=" + keyword;
        return getTranslationsFromUrl(url);
    }

    @Override
    public List<TranslateData> translateEngFi(String keyword) {
        String url = "http://www.sanakirja.org/search.php?l=3&l2=17&dont_switch_languages&q=" + keyword;
        return getTranslationsFromUrl(url);
    }

    @Override
    public TranslateResponse translateText(String text) {

        TranslateResponse translateResponse = new TranslateResponse(text);

        String[] words = text.split(" ");
        for (String word : words) {
            word = word.toLowerCase().replaceAll(",|\\.|!|\\?|\"", "");
            List<TranslateData> list1 = translateEngFi(word);
            List<TranslateData> list2 = translateFiEng(word);
            List<TranslateData> toUse;
            if (list1.size() > 0 && list2.size() > 0) {
                toUse = list1;
                toUse.addAll(list2);
            } else {
                if (list1.size() > 0) {
                    toUse = list1;
                } else {
                    toUse = list2;
                }
            }
            translateResponse.getWordMap().put(word, toUse);
        }
        return translateResponse;
    }
}
