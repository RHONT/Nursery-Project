package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.Handler.NurseryHandler;
import com.nursery.nursery_api.service.NurseryService;
import com.nursery.nursery_api.service.SendBotMessageService;
import com.nursery.nursery_api.service.SendBotMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component

public class TelegramBot extends TelegramLongPollingBot {

    private final NurseryService nurseryService;
    private final List<NurseryHandler> nurseryHandlerList;
    private final SendBotMessageService sendBotMessageService=new SendBotMessageServiceImpl(this);

    @Value("${telegram.bot.token}")
    private String token;

//    private final CommandContainer commandContainer;

    public TelegramBot(NurseryService nurseryService, List<NurseryHandler> nurseryHandlerList) {
        this.nurseryService = nurseryService;
        this.nurseryHandlerList = nurseryHandlerList;

//        this.commandContainer = new CommandContainer(new SendBotMessageServiceImpl(this));
    }


    @Override
    public void onUpdateReceived(Update update) {


        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!update.getMessage().getText().isEmpty()) {
                String message = "-main";
                Long chatIdUser=update.getMessage().getChatId();
                // если пользователь впервые
                if (!nurseryService.contain(chatIdUser)) {
                    try {
                        this.execute(SendMessage.
                                builder().
                                chatId(update.getMessage().getChatId()).
                                text("Здравствуйте, это питомник домашних животных!").
                                build());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                }
                checkMessage(message,chatIdUser);
            }
        } else if (update.hasCallbackQuery()){
            String message = update.getCallbackQuery().getData();
            Long idChat=update.getCallbackQuery().getMessage().getChatId();
            checkMessage(message,idChat);

//            String commandIdentifier = update.getCallbackQuery().getData();
//            commandContainer.retrieveCommand(commandIdentifier).execute(update);
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

    private void checkMessage(String message, Long chatId){
        for (var element:nurseryHandlerList) {
            if (element.supply(message)) {
                element.handle(chatId,this,nurseryService,sendBotMessageService);
                break;
            }
        }
    }
}

