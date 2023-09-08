package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class VolunteerCommand implements Command {

    private final SendBotMessageService sendBotMessageService;

    public final static String VOLUNTEER_MESSAGE = "https://t.me/margo171190";

    String[] buttonsName = {};
    String[] callData = {};

    public VolunteerCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                VOLUNTEER_MESSAGE, buttonsName, callData);

    }
}
