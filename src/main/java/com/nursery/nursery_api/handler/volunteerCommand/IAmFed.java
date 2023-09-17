package com.nursery.nursery_api.handler.volunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.VolunteerCommandHandler;
import com.nursery.nursery_api.service.ConnectService;
import org.springframework.stereotype.Component;

@Component
public class IAmFed implements VolunteerCommandHandler {
    @Override
    public void handle(Long idChat, TelegramBot bot, ConnectService connectService) {
        connectService.iAmGonnaWayVolunteer(idChat);
    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("Я ухожу");
    }
}
