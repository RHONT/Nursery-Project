package com.nursery.nursery_api.handler.buttonCreateCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class DogMainButton implements NurseryHandler {

    public final static String DOG_MESSAGE = "Dog shelter";

    String[] buttonsName = {"Информация о приюте", "Как забрать животное", "Отослать отчет о животном",
            "Связаться с волонтером", "К главному меню"};
    String[] callDataForDog = {"-info", "-dogAdopt", "-report", "-volunteer", "/main"};

    /**
     * пользователь делает выбор Собачьего приюта и его выбор прописывается в мапе Visitors "Собаки" value
     * создаются кнопки при выборе кнопки "Собачий приют"
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        nurseryDBService.setNurseryIntoVisitors(idChat, "Собаки");
        sendBotMessageService.sendMessage(idChat.toString(), DOG_MESSAGE, buttonsName, callDataForDog);

    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-dogs");
    }
}
