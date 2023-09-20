package com.nursery.nursery_api.service;

import com.nursery.nursery_api.bot.TelegramBot;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;

public class SendBotMessageServiceImpl implements SendBotMessageService {


    private final TelegramBot telegramBot;

    @Autowired
    public SendBotMessageServiceImpl(TelegramBot telegramBot) {

        this.telegramBot = telegramBot;
    }

    private final Logger logger = LoggerFactory.getLogger(ReportService.class);

    @Override
    public void sendMessage(String chatId, String message, String[] buttonsName, String[] callData) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setChatId(chatId);
        sendMessage.setText(message);

        SendMessage messageWithButtons = createButtons(sendMessage, buttonsName, callData);

        try {
            telegramBot.execute(messageWithButtons);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private SendMessage createButtons(SendMessage message, String[] buttonNames, String[] callData) {
        InlineKeyboardMarkup markupInline = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rowsInline = new ArrayList<>();

        for (int j = 0; j < buttonNames.length; j++) {
            List<InlineKeyboardButton> rowInline = new ArrayList<>();
            InlineKeyboardButton buttonName = new InlineKeyboardButton();
            buttonName.setText(buttonNames[j]);
            buttonName.setCallbackData(callData[j]);
            rowInline.add(buttonName);
            rowsInline.add(rowInline);
        }

        markupInline.setKeyboard(rowsInline);
        message.setReplyMarkup(markupInline);

        return message;

    }
}
