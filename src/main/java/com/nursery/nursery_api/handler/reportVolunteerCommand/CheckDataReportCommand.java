package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.DataReportHandler;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class CheckDataReportCommand implements DataReportHandler {

    private final PersonRepository personRepository;
    private final DataReportRepository dataReportRepository;

    public CheckDataReportCommand(PersonRepository personRepository, DataReportRepository dataReportRepository) {
        this.personRepository = personRepository;
        this.dataReportRepository = dataReportRepository;
    }

    public void handle(Long idChat, TelegramBot bot, Update update, ReportService reportService, SendBotMessageService sendBotMessageService){
        String input = update.getCallbackQuery().getData();
        long dataReportId = Long.parseLong(input.substring(input.lastIndexOf("|")+1));

        Optional<DataReport> dataReport = dataReportRepository.findById(dataReportId);
        dataReport.ifPresent(reportService::reportIsDoneSaveToBd);

        Optional<Long> idChatPerson=personRepository.findChatIdPersonByIdDataReport(dataReportId);

        if (idChatPerson.isPresent()) {
            try {
                bot.execute(
                        SendMessage.
                                builder().
                                chatId(idChatPerson.get()).
                                text("Отчет принят!").
                                build()
                );
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }
        }

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text("Вы приняли отчет.").
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    public boolean supply(String inputMessage){
        return inputMessage.startsWith("-check|");
    }
}
