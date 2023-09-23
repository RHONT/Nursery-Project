package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import static com.nursery.nursery_api.bot.StandartBotCommand.deleteButtons;

@Component
public class StartReportCheckCommand implements ReportHandler {

    @Override
    public void handle(Message message, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService) {
        Long idChat= message.getChatId();
        String[] buttonsName = { "Получить отчет","Перестать проверять отчеты", "Обновить отчеты", "Статистика"};
        String[] callDataMain = {"-getReport","-stopReportCheck","-refresh","-statistics"};



        reportService.reportModeActive(idChat);
        String ADOPT_MESSAGE="Проверка отчетов";

        sendBotMessageService.sendMessage(idChat.toString(), ADOPT_MESSAGE, buttonsName, callDataMain);
        deleteButtons(message);
    }



    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-startReportCheck");
    }
}
