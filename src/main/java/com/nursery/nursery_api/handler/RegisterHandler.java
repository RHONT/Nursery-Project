package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;


public interface RegisterHandler {
    void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService);

    boolean supply(String inputMessage);
}
