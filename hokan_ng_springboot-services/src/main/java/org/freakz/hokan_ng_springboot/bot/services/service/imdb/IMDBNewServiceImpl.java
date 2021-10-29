package org.freakz.hokan_ng_springboot.bot.services.service.imdb;


//import com.omertron.imdbapi.ImdbApi;
//import com.omertron.imdbapi.ImdbException;

import org.freakz.hokan_ng_springboot.bot.common.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBSearchResults;

/**
 * Created by Petri Airio (petri.j.airio@gmail.com) 14/11/2016 / 14.51
 */
public class IMDBNewServiceImpl implements IMDBNewService {

//    private final ImdbApi imdbApi = new ImdbApi();

    @Override
    public String parseSceneMovieName(String sceneName) {
        if (sceneName == null) {
            return null;
        }
        String[] parts = sceneName.split("\\.");
        String name = "";
        for (String part : parts) {
            if (part.toLowerCase().matches("\\d\\d\\d\\d|s\\d\\d?e\\d\\d?|\\d\\d?x\\d\\d?|\\d\\d\\d\\d?p")) {
                return name;
            }
            if (name.length() > 0) {
                name += " ";
            }
            name += part;
        }
        return null;
    }


    @Override
    public IMDBSearchResults imdbSearch(String title) {
/*        try {
            Map<String, Object> result = imdbApi.getSearchRawMap(title);
            IMDBSearchResults searchResults = new IMDBSearchResults();
            searchResults.setRawResults(result);
            return searchResults;
        } catch (ImdbException e) {
            e.printStackTrace();
        }*/
        return null;
    }

    @Override
    public IMDBDetails getDetailedInfo(String name) {
        return null;
    }
}
