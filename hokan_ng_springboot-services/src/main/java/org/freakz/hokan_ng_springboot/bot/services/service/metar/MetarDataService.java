package org.freakz.hokan_ng_springboot.bot.services.service.metar;

import org.freakz.hokan_ng_springboot.bot.common.models.MetarData;

import java.util.List;

/**
 * User: petria
 * Date: 11/26/13
 * Time: 1:05 PM
 *
 * @author Petri Airio <petri.j.airio@gmail.com>
 */
public interface MetarDataService {

    List<MetarData> getMetarData(Object... station);

}
