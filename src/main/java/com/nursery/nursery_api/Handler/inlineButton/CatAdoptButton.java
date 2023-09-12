package com.nursery.nursery_api.Handler.inlineButton;

import com.nursery.nursery_api.Handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class CatAdoptButton implements NurseryHandler {

    public final static String ADOPT_MESSAGE = "Здравствуйте! Здесь Вы можете узнать информацию о том, " +
            "как забрать кошку.";
    String[] buttonsNameForCat = {"Правила знакомства с животным до того, как забрать его из приюта.",
            "Список документов, необходимых для того, чтобы взять животное ",
            "Список рекомендаций по транспортировке животного",
            "Список рекомендаций по обустройству дома для котенка",
            "Список рекомендаций по обустройству дома для взрослой кошки",
            "Список рекомендаций по обустройству дома для кошки-инвалида",
            "Список причин, почему могут отказать и не дать забрать кошку из приюта.  ",
            "Оставить свои контактные данные",
            "Связаться с волонтером",
            "К главному меню"};

    String[] callDataCat = {"-knowPet",
            "-docs", "-transportation",
            "-baby",
            "-adult",
            "-disabled",
            "-refusal",
            "-contactCat",
            "-volunteer",
            "-main"};

    /**
     * создаются кнопки при выборе кнопки у меню Кошачьего приюта "Как забрать животное"
     *
     * @param idChat
     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService) {
        sendBotMessageService.sendMessage(idChat.toString(), ADOPT_MESSAGE, buttonsNameForCat, callDataCat);
    }

    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-catAdopt");
    }
}
