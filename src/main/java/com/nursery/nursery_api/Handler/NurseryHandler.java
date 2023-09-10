package com.nursery.nursery_api.Handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryService;
import com.nursery.nursery_api.service.SendBotMessageService;

public interface NurseryHandler {
    void handle(Long idChat, TelegramBot bot, NurseryService nurseryService,SendBotMessageService sendBotMessageService);
    boolean supply(String inputMessage);
}
