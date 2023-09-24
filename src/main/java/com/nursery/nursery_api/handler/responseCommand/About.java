package com.nursery.nursery_api.handler.responseCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import static com.nursery.nursery_api.bot.StandartBotCommand.sendOnlyText;

@Component
@RequiredArgsConstructor
public class About implements NurseryHandler {
    /**
     * вывод текста при нажатии кнопки "Общая информация"
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {

        sendOnlyText(idChat,nurseryDBService.getMeAboutNursery(idChat));
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
       return inputMessage.equals("-about");
    }
}
