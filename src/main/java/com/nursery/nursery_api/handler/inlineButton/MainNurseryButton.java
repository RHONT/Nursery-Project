package com.nursery.nursery_api.handler.inlineButton;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class MainNurseryButton implements NurseryHandler {
    public final static String START_MESSAGE = "Выберите приют, который Вас интересует.";
    String[] buttonsName = {"Кошачий приют", "Собачий приют"};
    String[] callDataMain = {"-cats", "-dogs"};
    /**
     * Создаются кнопки при вводе любого текста
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        // тут код, который кидает в телеграм кнопки выбора Собак или Кошек
        sendBotMessageService.sendMessage(idChat.toString(), START_MESSAGE, buttonsName, callDataMain);
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-main");
    }
}
