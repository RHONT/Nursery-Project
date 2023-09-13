package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.handler.VolunteerHandler;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
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

    private final VolunteerRepository volunteerRepository;
    private final NurseryDBService nurseryDBService;
    private final List<NurseryHandler> nurseryHandlerList;
    private final List<VolunteerHandler> volunteerHandlers;
    private final SendBotMessageService sendBotMessageService=new SendBotMessageServiceImpl(this);
    private final ConnectService connectService=new ConnectService(this);

    @Value("${telegram.bot.token}")
    private String token;

//    private final CommandContainer commandContainer;

    public TelegramBot(VolunteerRepository volunteerRepository, NurseryDBService nurseryDBService, List<NurseryHandler> nurseryHandlerList, List<VolunteerHandler> volunteerHandlers) {
        this.volunteerRepository = volunteerRepository;
        this.nurseryDBService = nurseryDBService;
        this.nurseryHandlerList = nurseryHandlerList;
        this.volunteerHandlers = volunteerHandlers;
    }


    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!update.getMessage().getText().isEmpty()) {
                String message = "-main";
                Long chatIdUser=update.getMessage().getChatId();

                if (connectService.containInActiveDialog(chatIdUser)) {
                    if (connectService.isPerson(chatIdUser)) {

                        try {
                            this.execute(SendMessage.
                                    builder().
                                    chatId(connectService.getVolunteerChatIdByPersonChatId(chatIdUser)).
                                    text(update.getMessage().getText()).
                                    build());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }

                    } else {
                        try {
                            this.execute(SendMessage.
                                    builder().
                                    chatId(chatIdUser).
                                    text(update.getMessage().getText()).
                                    build());
                        } catch (TelegramApiException e) {
                            e.printStackTrace();
                        }
                    }

                } else {

                    // если пользователь впервые
                    if (!nurseryDBService.contain(chatIdUser)) {
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
            }
        } else if (update.hasCallbackQuery()){
            String message = update.getCallbackQuery().getData();
            Long idChat=update.getCallbackQuery().getMessage().getChatId();
            checkMessage(message,idChat);
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
                element.handle(chatId,this, nurseryDBService,sendBotMessageService);
                break;
            }
        }

        for (var element:volunteerHandlers) {
            if (element.supply(message)) {
                element.handle(chatId,this, connectService);
                break;
            }
        }


    }
}

