package com.nursery.nursery_api.Handler.inlineButton;

import com.nursery.nursery_api.Handler.NurseryHandler;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class CatMainButton implements NurseryHandler {


    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryService nurseryService, SendBotMessageService sendBotMessageService) {
        String[] buttonsName = {"Info about the shelter", "How to adopt a pet", "Send a report about a pet",
                "Send a message to a volunteer"};
        String[] callDataForCat = {"About", "catAdopt", "report", "volunteer"};
        // так же пользовательс сделал выбор и его выбор нужно прописать в Visitors value
        // Тут я забиваю хардкодом. Можно потом подумать как сюда красиво внедрить переменные
        // как вариант сделать мапу с сопоставленеием к примеру "-cats" : Кошки"
        // Так как у нас есть inputMessage, мы сможем отсюда дернуть значение и подставить его сюда.
        nurseryService.setNurseryIntoVisitors(idChat, "Кошки");
        sendBotMessageService.sendMessage(idChat.toString(), "Cat shelter", buttonsName, callDataForCat);
        // тут выкидываем кнопки для кошачьего приюта
    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-cats");
    }
}
