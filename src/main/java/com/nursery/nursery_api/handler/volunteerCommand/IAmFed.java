package com.nursery.nursery_api.handler.volunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.VolunteerCommandHandler;
import com.nursery.nursery_api.service.ConnectService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class IAmFed implements VolunteerCommandHandler {
    @Override
    public void handle(Long idChat, TelegramBot bot, ConnectService connectService) {

        connectService.iAmGonnaWayVolunteer(idChat);

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text("Вы покинули наш коллектив. Будем вам рады всегда!").
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("Я ухожу");
    }
}
