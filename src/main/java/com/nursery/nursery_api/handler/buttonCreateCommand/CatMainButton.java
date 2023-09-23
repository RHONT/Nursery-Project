package com.nursery.nursery_api.handler.buttonCreateCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class CatMainButton implements NurseryHandler {

    public final static String CAT_MESSAGE = "Cat shelter";

    String[] buttonsName = {"Информация о приюте", "Как забрать животное", "Отослать отчет о животном",
            "Связаться с волонтером", "К главному меню"};
    String[] callDataForCat = {"-info", "-catAdopt", "-report", "-volunteer", "-main"};

    /**
     * пользователь делает выбор Кошачьего приюта и его выбор прописывается в мапе Visitors "Кошки" value
     * создаются кнопки при выборе кнопки "Кошачий приют"
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        nurseryDBService.setNurseryIntoVisitors(idChat, "Кошки");
        sendBotMessageService.sendMessage(idChat.toString(), CAT_MESSAGE, buttonsName, callDataForCat);
    }

    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-cats");
    }
}
