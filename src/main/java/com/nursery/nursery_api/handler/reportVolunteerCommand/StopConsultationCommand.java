package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class StopConsultationCommand implements ReportHandler {

    @Override
    public void handle(Long idChat, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService) {

        connectService.stopWorkVolunteer(idChat);
        String response = "Вы перестали консультировать людей. Спасибо за ваш труд. Ждем вас еще.";

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text(response).
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-stopConsultation");
    }
}