package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.ConnectService;

public interface VolunteerCommandHandler {
    void handle(Long idChat, TelegramBot bot, ConnectService connectService);

    boolean supply(String inputMessage);
}
