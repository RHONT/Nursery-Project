package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class ReportCommand implements Command{

    private final SendBotMessageService sendBotMessageService;

    public final static String REPORT_MESSAGE = "Here you can send a daily report about your pet.";

    String[] buttonsName = {"Send a report", "Send a message to a volunteer"};

    String[] callData = {"dailyReport", "volunteer"};

    public ReportCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                REPORT_MESSAGE, buttonsName, callData);

    }
}
