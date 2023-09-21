package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.DataReportHandler;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.stream.Collectors;

public class UnCheckDataReportCommand implements DataReportHandler {

    public void handle(Long idChat, TelegramBot bot, Update update, ReportService reportService, SendBotMessageService sendBotMessageService){
        String input = update.getCallbackQuery().getData();
        long dataReportId = Long.valueOf(input.substring(input.lastIndexOf("|")+1));
        DataReport dataReport = reportService.getDataReportQueue().stream().
                filter(report -> report.getIdDataReport() == dataReportId).
                collect(Collectors.toList()).get(0);
        reportService.reportIsBadNotSaveToBd(dataReport);

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text("Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так " +
                                    "подробно, как необходимо. Пожалуйста, подойди ответственнее к " +
                                    "этому занятию. В противном случае волонтеры приюта будут обязаны " +
                                    "самолично проверять условия содержания животного").
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    public boolean supply(String inputMessage){
        return inputMessage.startsWith("-unCheck|");
    }
}
