package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.handler.VolunteerCommandHandler;
import com.nursery.nursery_api.handler.VolunteerHandler;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import com.nursery.nursery_api.service.SendBotMessageServiceImpl;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Component

public class TelegramBot extends TelegramLongPollingBot {

    private final NurseryDBService nurseryDBService;
    private final List<NurseryHandler> nurseryHandlerList;
    private final List<VolunteerHandler> volunteerHandlers;
    private final List<VolunteerCommandHandler> volunteerCommandHandlers;
    private final SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(this);
    private final ConnectService connectService;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(NurseryDBService nurseryDBService,
                       List<NurseryHandler> nurseryHandlerList,
                       List<VolunteerHandler> volunteerHandlers,
                       List<VolunteerCommandHandler> volunteerCommandHandlers,
                       @Lazy ConnectService connectService) {
        this.nurseryDBService = nurseryDBService;
        this.nurseryHandlerList = nurseryHandlerList;
        this.volunteerHandlers = volunteerHandlers;
        this.volunteerCommandHandlers = volunteerCommandHandlers;
        this.connectService = connectService;
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasText()) {
            if (!update.getMessage().getText().isEmpty()) {
                String message = "-main";
                Long chatIdUser = update.getMessage().getChatId();

                if (connectService.containInActiveDialog(chatIdUser)) {   // является ли chat id участником активной беседы?
                    if (!connectService.isPerson(chatIdUser)) {           // chat id - это волонтер?
                        String checkedMessage = update.getMessage().getText();
                        checkVolunteerOperation(checkedMessage, chatIdUser);
                        return;
                    }

                    if (connectService.isPerson(chatIdUser)) {    // если chat id вопрошающий, то шлем сообщение волонтеру
                        sendSimpleText(connectService.getVolunteerChatIdByPersonChatId(chatIdUser), update.getMessage().getText());

                    } else {                                        // если нет, то наоборот
                        sendSimpleText(connectService.getPersonChatIdByChatIdVolunteer(chatIdUser), update.getMessage().getText());
                    }

                } else {

                    // если пользователь впервые
                    if (!nurseryDBService.contain(chatIdUser)) {
                        sendSimpleText(update.getMessage().getChatId(), "Здравствуйте, это питомник домашних животных!");
                    }
                    checkMessage(message, chatIdUser);       // При любой непонятной команде выводим главное меню чата
                }
                // Регистрация волонтера. Тут же id_chat попадает в базу
                if (update.getMessage().getText().startsWith("Хочу стать волонтером")) {
                    Volunteer volunteer = new Volunteer();
                    volunteer.setBusy(false);
                    volunteer.setVolunteerChatId(chatIdUser);
                    connectService.addNewVolunteer(volunteer);
                }
                if (update.getMessage().getText().startsWith("Я ухожу")) {
                    connectService.hasLeftVolunteer(chatIdUser);
                }
            }
            // проверяем ответы от кнопок
        } else if (update.hasCallbackQuery()) {
            String message = update.getCallbackQuery().getData();
            Long idChat = update.getCallbackQuery().getMessage().getChatId();
            checkMessage(message, idChat);
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

    /**
     * @param message - строка берется из CallbackQuery. Это значение, что лежит "под кнопкой"
     * @param chatId
     */
    private void checkMessage(String message, Long chatId) {
        for (var element : nurseryHandlerList) {
            if (element.supply(message)) {
                element.handle(chatId, this, nurseryDBService, sendBotMessageService);
                break;
            }
        }

        for (var element : volunteerHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this, connectService);
                break;
            }
        }
    }

    /**
     * @param message         - строк приходит от волонтера
     * @param chatIdVolunteer Проверяем является ли эта строка командой.
     */
    private void checkVolunteerOperation(String message, Long chatIdVolunteer) {
        for (var element : volunteerCommandHandlers) {
            if (element.supply(message)) {
                element.handle(chatIdVolunteer, this, connectService);
                break;
            }
        }
    }

    /**
     * @param chatId
     * @param message Отправляем стандартное сообщение через бот пользователю
     */
    private void sendSimpleText(Long chatId, String message) {
        try {
            this.execute(SendMessage.
                    builder().
                    chatId(chatId).
                    text(message).
                    build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }
}

