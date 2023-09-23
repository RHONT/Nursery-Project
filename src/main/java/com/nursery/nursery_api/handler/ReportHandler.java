package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.*;
import org.telegram.telegrambots.meta.api.objects.Message;

public interface ReportHandler {
    void handle(Message message, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService);

    boolean supply(String inputMessage);
}
