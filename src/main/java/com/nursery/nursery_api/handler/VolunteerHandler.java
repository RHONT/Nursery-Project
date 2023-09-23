package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;

public interface VolunteerHandler {
    void handle(Long idChat, TelegramBot bot, ConnectService connectService);

    boolean supply(String inputMessage);
}
