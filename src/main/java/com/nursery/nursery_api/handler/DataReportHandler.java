package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public interface DataReportHandler {
    void handle(Long idChat, TelegramBot bot, Update update, ReportService reportService);
    boolean supply(String inputMessage);
}
