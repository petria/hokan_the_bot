package org.freakz.hokan_ng_springboot.bot.services.service.imdb;


import org.freakz.hokan_ng_springboot.bot.common.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBSearchResults;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
public interface IMDBService {

    IMDBSearchResults findByTitle(String title);

    IMDBDetails getDetailedInfo(String name);

}
