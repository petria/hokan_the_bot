package org.freakz.hokan_ng_springboot.bot.services.service.imdb;

import org.freakz.hokan_ng_springboot.bot.common.models.IMDBDetails;
import org.freakz.hokan_ng_springboot.bot.common.models.IMDBSearchResults;
import org.springframework.stereotype.Service;

/**
 * Created by Petri Airio on 17.11.2015.
 * -
 */
@Service
public class IMDBServiceImpl implements IMDBService {

    @Override
    public IMDBSearchResults findByTitle(String title) {
        return null;
    }

    @Override
    public IMDBDetails getDetailedInfo(String name) {
        return null;
    }
}
