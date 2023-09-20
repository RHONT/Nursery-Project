package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Component
public class ModeCommand implements NurseryHandler {

    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text("").
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-mode");
    }
}
