package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class MainCommand implements ReportHandler {
    public final static String MAIN_MESSAGE = "Выберите опцию";
    // вывод всех вариантов
    String[] buttonsName = {"Мой статус работы", "Начать проверку отчетов", "Прекратить консультации", "Начать консультировать", "Статистика"};
    String[] callDataMain = {"-myMode","-startReportCheck","-stopConsultation","-startConsulting","-statistics"};
    /**
     * Создаются кнопки при вводе любого текста

     * @param bot
     * @param nurseryDBService
     * @param sendBotMessageService
     */
    @Override
    public void handle(Message message, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService) {

        sendBotMessageService.sendMessage(message.getChatId().toString(), MAIN_MESSAGE, buttonsName, callDataMain);
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("/main_volunteer");
    }
}
