package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class CatAndDogCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public final static String CAT_MESSAGE = "Cat shelter";
    public final static String DOG_MESSAGE = "Dog shelter";

    String[] buttonsName = {"Info about the shelter", "How to adopt a pet", "Send a report about a pet",
            "Send a message to a volunteer"};

    String[] callDataForDog = {"info", "dogAdopt", "report",
            "volunteer"};

    // Заменили info - > About for test
    String[] callDataForCat = {"About", "catAdopt", "report",
            "volunteer"};

    public CatAndDogCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        if (update.getCallbackQuery().getData().equals("cat")) {

            sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                    CAT_MESSAGE, buttonsName, callDataForCat);
        } else if (update.getCallbackQuery().getData().equals("dog")) {
            sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                    DOG_MESSAGE, buttonsName, callDataForDog);
        }
    }
}
