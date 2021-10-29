package org.freakz.hokan_ng_springboot.bot.services.service.lunch;

import org.freakz.hokan_ng_springboot.bot.common.enums.LunchPlace;
import org.freakz.hokan_ng_springboot.bot.common.models.LunchData;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Petri Airio on 21.1.2016.
 * -
 */
public interface LunchService {

    LunchData getLunchForDay(LunchPlace place, LocalDateTime day);

    List<LunchPlace> getLunchPlaces();

}
