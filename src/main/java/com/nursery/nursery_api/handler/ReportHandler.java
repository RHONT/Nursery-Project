package com.nursery.nursery_api.handler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.*;

public interface ReportHandler {
    void handle(Long idChat, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService,ConnectService connectService);

    boolean supply(String inputMessage);
}
