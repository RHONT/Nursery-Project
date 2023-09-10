package com.nursery.nursery_api.Handler.inlineButton;

import com.nursery.nursery_api.Handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class MainNurseryButton implements NurseryHandler {
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryService nurseryService, SendBotMessageService sendBotMessageService) {
        // тут код, который кидает в телеграм кнопки выбора Собак или Кошек

    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-main");
    }
}
