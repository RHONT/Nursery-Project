package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class StartCommand implements Command {

    private final SendBotMessageService sendBotMessageService;

    public final static String START_MESSAGE = "Hello! It's the animal shelter. {Information about " +
            "the shelter}.Please choose a shelter you are interested in.";

    String[] buttonsName = {"Cat shelter", "Dog Shelter"};
    String[] callData = {"cat", "dog"};


    public StartCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(update.getMessage().getChatId().toString(),
                START_MESSAGE, buttonsName, callData);
    }
}
