package com.nursery.nursery_api.testHandler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import com.nursery.nursery_api.service.SendBotMessageServiceImpl;
import org.springframework.stereotype.Component;

@Component
public class ButtonComponent{

    public final TelegramBot bot;
    public final NurseryDBService nurseryDBService;
    public final SendBotMessageService sendBotMessageService;

    public ButtonComponent(TelegramBot bot, NurseryDBService nurseryDBService) {
        this.bot = bot;

        this.nurseryDBService = nurseryDBService;
        this.sendBotMessageService = new SendBotMessageServiceImpl(bot);
    }

    public TelegramBot getBot() {
        return bot;
    }

    public NurseryDBService getNurseryDBService() {
        return nurseryDBService;
    }

    public SendBotMessageService getSendBotMessageService() {
        return sendBotMessageService;
    }
}
