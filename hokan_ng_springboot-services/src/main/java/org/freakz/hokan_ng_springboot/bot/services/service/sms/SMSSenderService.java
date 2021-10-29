package org.freakz.hokan_ng_springboot.bot.services.service.sms;

public interface SMSSenderService {

    String sendSMS(String from, String to, String message);

    String getSMSCredits();

}
