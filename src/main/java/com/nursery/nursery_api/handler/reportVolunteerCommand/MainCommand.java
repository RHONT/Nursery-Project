package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class MainCommand implements NurseryHandler {
    public final static String MAIN_MESSAGE = "";
    String[] buttonsName = {"Проверка режима", "Войти в режим проверки отчетов", "Статистика"};
    String[] callDataMain = {"-mode", "-reportCheck", "-statistics"};
    /**
     * Создаются кнопки при вводе любого текста
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        sendBotMessageService.sendMessage(idChat.toString(), MAIN_MESSAGE, buttonsName, callDataMain);
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-mainVolunteer");
    }
}
