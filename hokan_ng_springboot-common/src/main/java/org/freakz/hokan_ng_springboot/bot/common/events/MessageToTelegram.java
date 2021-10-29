package org.freakz.hokan_ng_springboot.bot.common.events;

import java.io.Serializable;

/**
 * Created by Petri Airio on 1.9.2017.
 */
public class MessageToTelegram implements Serializable {

    private String message;

    private String telegramChatId;


    public MessageToTelegram() {
    }

    public MessageToTelegram(String message, String telegramChatId) {
        this.message = message;
        this.telegramChatId = telegramChatId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getTelegramChatId() {
        return telegramChatId;
    }

    public void setTelegramChatId(String telegramChatId) {
        this.telegramChatId = telegramChatId;
    }
}
