package org.freakz.hokan_ng_springboot.bot.common.service.translate;

import org.freakz.hokan_ng_springboot.bot.common.models.TranslateData;
import org.freakz.hokan_ng_springboot.bot.common.models.TranslateResponse;

import java.util.List;

/**
 * Created by Petri Airio on 12.11.2015.
 * -
 */
public interface SanakirjaOrgTranslateService {

    List<TranslateData> translateFiEng(String keyword);

    List<TranslateData> translateEngFi(String keyword);

    TranslateResponse translateText(String text);


}
