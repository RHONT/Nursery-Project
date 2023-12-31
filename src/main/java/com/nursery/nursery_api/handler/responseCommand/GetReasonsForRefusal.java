package com.nursery.nursery_api.handler.responseCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
@RequiredArgsConstructor
public class GetReasonsForRefusal implements NurseryHandler {
    /**
     * вывод текста при нажатии кнопки "Список причин, почему могут отказать и не дать забрать кошку\собаку из приюта."
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text(nurseryDBService.getReasonsRefusal(idChat)).
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
    public boolean supply(String inputMessage) {return inputMessage.equals("-refusal");}
}
