package org.freakz.hokan_ng_springboot.bot.services.service.alko;

import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequest;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceRequestType;
import org.freakz.hokan_ng_springboot.bot.common.events.ServiceResponse;
import org.freakz.hokan_ng_springboot.bot.common.models.AlkoSearchResults;
import org.freakz.hokan_ng_springboot.bot.services.service.annotation.ServiceMessageHandler;
import org.freakz.hokan_ng_springboot.bot.services.service.wwwfetcher.WWWPageFetcher;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 22/11/2016 / 8.55
 */
@Service
public class AlkoSearchServiceRequestHandler implements AlkoSearchService {

    private WWWPageFetcher wwwPageFetcher;

    @Autowired
    public void setWwwPageFetcher(WWWPageFetcher wwwPageFetcher) {
        this.wwwPageFetcher = wwwPageFetcher;
    }

    private String fetchAlkoPage(String url) {
        String output;
        output = wwwPageFetcher.fetchWWWPage(url, false);
        return output;
    }

    @Override
    public AlkoSearchResults alkoSearch(String search) {
        AlkoSearchResults alkoSearchResults = new AlkoSearchResults();
        List<String> results = new ArrayList<>();
        alkoSearchResults.setResults(results);

        try {
            String term = URLEncoder.encode(search, "UTF-8");
            String url = "https://www.alko.fi/tuotehaku?SortingAttribute=random_185&SearchParameter=&SelectedFilter=&SearchTerm=" + term;
            String page = fetchAlkoPage(url);
            Document doc = Jsoup.parse(page);
            Elements list2 = doc.getElementsByClass("mini-product-wrap");
            for (Element listItem : list2) {
                String listItemText = listItem.text();
                String item = listItemText.replaceAll(" Cart Icon Zoom Icon ", " / ").replaceFirst(" ", ".").replaceFirst(" ", "€ / ").replaceAll("/ /", "/");
                item = item.replaceAll("ale ale", " / ale");
                item = item.replaceAll("lager lager", " / lager");
                item = item.replaceAll("tumma lager tumma lager", " / tumma lager");
                item = item.replaceAll("vahva lager vahva lager", " / vahva lager");
                results.add(String.format("%s", item));
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return alkoSearchResults;
    }

    @ServiceMessageHandler(ServiceRequestType = ServiceRequestType.ALKO_SEARCH_REQUEST)
    public void alkoSearchRequestHandler(ServiceRequest request, ServiceResponse response) {
        String search = (String) request.getParameters()[0];
        AlkoSearchResults alkoSearchResults = alkoSearch(search);
        response.setResponseData(request.getType().getResponseDataKey(), alkoSearchResults);

    }

}
