package com.nursery.nursery_api.Handler.responseForPerson;

import com.nursery.nursery_api.Handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryService;
import com.nursery.nursery_api.service.SendBotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
@Component
@RequiredArgsConstructor
public class GetSafetyInstructions implements NurseryHandler {
    /**
     * вывод текста при нажатии кнопки "Общие рекомендации по безопасности"
     * @param idChat
     * @param bot
     * @param nurseryService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryService nurseryService, SendBotMessageService sendBotMessageService) {
        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text(nurseryService.getAccident_prevention(idChat)).
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
        return inputMessage.equals("-safety");
    }
}
