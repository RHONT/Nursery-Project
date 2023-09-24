package com.nursery.nursery_api.bot;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.DeleteMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StandartBotCommand {
    private static TelegramBot bot;

    @Autowired
    public StandartBotCommand(TelegramBot bot) {
        this.bot = bot;
    }


    public static void sendOnlyText(Long chatId, String message) {
        try {
            bot.execute(SendMessage.
                    builder().
                    chatId(chatId).
                    text(message).
                    build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    public static void deleteButtons(Message message) {
        try {
            bot.execute(new DeleteMessage(message.getChatId().toString(), message.getMessageId()));
        } catch (
                TelegramApiException e) {
            e.printStackTrace();
        }
    }
}

