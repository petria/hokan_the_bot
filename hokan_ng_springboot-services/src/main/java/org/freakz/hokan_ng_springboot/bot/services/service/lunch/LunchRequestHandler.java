package org.freakz.hokan_ng_springboot.bot.services.service.lunch;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;

import java.time.LocalDateTime;


/**
 * Created by Petri Airio on 22.1.2016.
 * -
 */
public interface LunchRequestHandler {

    void handleLunchPlace(LunchPlace lunchPlaceRequest, LunchData response, LocalDateTime day);

}
