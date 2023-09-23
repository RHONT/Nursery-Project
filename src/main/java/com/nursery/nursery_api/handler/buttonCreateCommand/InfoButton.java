package com.nursery.nursery_api.handler.buttonCreateCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class InfoButton implements NurseryHandler {

    public final static String INFO_MESSAGE = "Здравствуйте! Здесь Вы можете найти информацию о приюте. " +
            "Выберите,что Вы хотите узнать.";

    String[] buttonsName = {"Общая информация", "Режим работы, адрес, " +
            "Схема проезда", "Получить пропуск на машину", "Общие рекомендации по безопасности",
            "Оставить свои контактные данные", "Отослать сообщение волонтеру.", "К главному меню"};

    String[] callDataInfo = {"-about", "-address", "-pass", "-safety", "-contact", "-volunteer", "/main"};

    /**
     * Создаются кнопки при нажатии на кнопку "Информация о приюте"
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        sendBotMessageService.sendMessage(idChat.toString(), INFO_MESSAGE, buttonsName, callDataInfo);

    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-info");
    }
}

