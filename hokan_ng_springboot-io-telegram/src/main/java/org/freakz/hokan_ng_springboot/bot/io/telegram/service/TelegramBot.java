package org.freakz.hokan_ng_springboot.bot.io.telegram.service;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;


public class TelegramBot extends TelegramLongPollingBot {

    private final TelegramConnectService connectService;

    public TelegramBot(TelegramConnectService connectService) {
        this.connectService = connectService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String from;
            Integer userId;
            User fromUser = update.getMessage().getFrom();
            if (update.getMessage().getFrom().getUserName() != null && update.getMessage().getFrom().getUserName().length() > 0) {
                from = update.getMessage().getFrom().getUserName();
            } else {
                from = update.getMessage().getFrom().getFirstName() + update.getMessage().getFrom().getLastName();
            }
            connectService.sendMessageToEngine(update.getMessage().getText(), from, update.getMessage().getChatId(), fromUser.getId());
        }
    }

    @Override
    public String getBotUsername() {
        return "HokanThebot";
    }

    @Override
    public String getBotToken() {
        return "412991638:AAGHKCGzfpGrKTLGOSdy81oaknxE-Qw2a20";
    }

}
