package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.DataReportHandler;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.ByteArrayInputStream;

@Component
public class GetDataReport implements DataReportHandler {
    @Override
    public void handle(Long idChat, TelegramBot bot, Update update, ReportService reportService) {

        DataReport dataReport = reportService.getOneDataReport();

        SendPhoto sendPhoto = new SendPhoto();

        byte[] foto = dataReport.getFoto();

        sendPhoto.setChatId(update.getMessage().getChatId());
        InputFile inputFile = new InputFile(new ByteArrayInputStream(foto), "photo.jpg");
        sendPhoto.setPhoto(inputFile); // Установка фотографии в объекте SendPhoto
        sendPhoto.setCaption(dataReport.getMessagePerson());

        try {
            bot.execute(sendPhoto);
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-getReport");
    }
}
