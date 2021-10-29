package org.freakz.hokan_ng_springboot.bot.services.service.nimipaiva;

import org.freakz.hokan_ng_springboot.bot.common.models.NimipaivaData;

import java.time.LocalDateTime;


/**
 * Created by Petri Airio on 5.10.2015.
 * -
 */
public interface NimipaivaService {

    NimipaivaData getNamesForDay(LocalDateTime day);

    NimipaivaData findDayForName(String name);

    void loadNames();

}
