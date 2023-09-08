package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class InfoCommand implements Command {

    private final SendBotMessageService sendBotMessageService;

    public final static String INFO_MESSAGE = "Hello! Here you can find information about the shelter. " +
            "Please choose what you want to know.";

    String[] buttonsName = {"General information", "Opening hours, address, " +
            "driving directions", "Get a pass to your car", "General safety recommendations",
            "Leave your contact information", "Send a message to a volunteer"};

    String[] callData = {"general", " address", "pass", "safety", "contact", "volunteer"};

    public InfoCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                INFO_MESSAGE, buttonsName, callData);

    }
}
