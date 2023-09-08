package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.command.CommandContainer;
import com.nursery.nursery_api.service.SendBotMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
public class TelegramBot extends TelegramLongPollingBot {

    @Value("${telegram.bot.token}")
    private String token;

    private final CommandContainer commandContainer;

    public TelegramBot() {
        this.commandContainer = new CommandContainer(new SendBotMessageServiceImpl(this));
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            String message = update.getMessage().getText().trim();
            if (!update.getMessage().getText().isEmpty()) {
                String commandIdentifier = "start";

                commandContainer.retrieveCommand(commandIdentifier).execute(update);
            }
        } else if (update.hasCallbackQuery()){
            String callData = update.getCallbackQuery().getData();
            String commandIdentifier = callData;
            commandContainer.retrieveCommand(commandIdentifier).execute(update);
        }
    }

    @Override
    public String getBotUsername() {
        return "animal-shelter-test";
    }

    @Override
    public String getBotToken() {
        return token;
    }
}

