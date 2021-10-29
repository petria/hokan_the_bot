package org.freakz.hokan_ng_springboot.bot.services.service.sms;

import org.freakz.hokan_ng_springboot.bot.services.config.RuntimeConfig;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URLEncoder;

@Service
public class SMSSenderServiceImpl implements SMSSenderService {

    private static final org.slf4j.Logger log = LoggerFactory.getLogger(SMSSenderServiceImpl.class);

    private final RuntimeConfig config;

    @Autowired
    public SMSSenderServiceImpl(RuntimeConfig config) {
        this.config = config;
    }

    @Override
    public String sendSMS(String from, String to, String message) {
        log.debug("from: {} - to: {} -> {}", from, to, message);

        try {
            String url = String.format("https://api.budgetsms.net/sendsms/?credit=1&price=1&mccmnc=1&username=%s&userid=%s&handle=%s&msg=%s&from=%s&to=%s",
                    config.getSmsSendUsername(),
                    config.getSmsSendUserId(),
                    config.getSmsSendHandle(),
                    URLEncoder.encode(message, "UTF-8"), from, to);
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.getElementsByTag("body");
            return body.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public String getSMSCredits() {
        log.debug("Check SMS credits");
        try {
            String url = String.format("https://api.budgetsms.net/checkcredit/?username=%s&userid=%s&handle=%s",
                    config.getSmsSendUsername(),
                    config.getSmsSendUserId(),
                    config.getSmsSendHandle());
            Document doc = Jsoup.connect(url).get();
            Elements body = doc.getElementsByTag("body");
            return body.text();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


}
