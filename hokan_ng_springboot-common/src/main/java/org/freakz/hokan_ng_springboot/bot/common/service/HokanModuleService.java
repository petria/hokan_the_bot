package org.freakz.hokan_ng_springboot.bot.common.service;

import org.freakz.hokan_ng_springboot.bot.common.enums.HokanModule;

/**
 * Created by Petri Airio on 15.5.2015.
 */
public interface HokanModuleService {

//  void setHokanModule(HokanModule module);

    HokanModule getHokanModule();

    long getSessionId();

}
