package org.freakz.hokan_ng_springboot.bot.services.service.currency;

import org.freakz.hokan_ng_springboot.bot.common.models.GoogleCurrency;

import java.util.List;

/**
 * Created by Petri Airio on 2.9.2015.
 * -
 */
public interface CurrencyService {


    List<GoogleCurrency> getGoogleCurrencies();

    String googleConvert(String amount, String from, String to);


}
