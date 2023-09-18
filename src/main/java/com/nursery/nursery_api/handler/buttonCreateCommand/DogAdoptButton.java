package com.nursery.nursery_api.handler.buttonCreateCommand;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class DogAdoptButton implements NurseryHandler {
    public final static String ADOPT_MESSAGE = "Здравствуйте! Здесь Вы можете узнать информацию о том, " +
            "как забрать собаку.";
    String[] buttonsNameForDog = {"Правила знакомства с животным до того, как забрать его из приюта.",
            "Список документов, необходимых для того, чтобы взять животное",
            "Список рекомендаций по транспортировке животного",
            "Список рекомендаций по обустройству дома для щенка",
            "Список рекомендаций по обустройству дома для взрослой собаки",
            "Список рекомендаций по обустройству дома для собаки-инвалида",
            "Cоветы кинолога по первичному общению с собакой",
            "Рекомендации по проверенным кинологам для дальнейшего обращения к ним",
            "Список причин, почему могут отказать и не дать забрать кошку из приюта.  ",
            "Оставить свои контактные данные",
            "Связаться с волонтером",
            "К главному меню"};

    String[] callDataDog = {"-knowPet",
            "-docs", "-transportation",
            "-baby",
            "-adult",
            "-disabled",
            "-cynologistDog",
            "-cynologistsDog",
            "-refusal",
            "-contactDog",
            "-volunteer",
            "-main"};

    /**
     * создаются кнопки при выборе кнопки у меню Собачьего приюта "Как забрать животное"
     *
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        sendBotMessageService.sendMessage(idChat.toString(), ADOPT_MESSAGE, buttonsNameForDog, callDataDog);
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-dogAdopt");
    }
}
