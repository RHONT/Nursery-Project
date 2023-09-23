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
public class StopReportCheckCommand implements ReportHandler {

    public final static String MAIN_MESSAGE = "Выберите опцию";
    // вывод всех вариантов
    String[] buttonsName = {"Мой статус работы", "Начать проверку отчетов", "Прекратить консультации", "Начать консультировать", "Статистика"};
    String[] callDataMain = {"-myMode","-startReportCheck","-stopConsultation","-startConsulting","-statistics"};
    @Override
    public void handle(Message message, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService) {

        Long idChat= message.getChatId();
        reportService.reportModeDisable(idChat);
        String response="Режим проверки отключен";

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

        sendBotMessageService.sendMessage(idChat.toString(), MAIN_MESSAGE, buttonsName, callDataMain);
        deleteButtons(message);
    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-stopReportCheck");
    }
}
