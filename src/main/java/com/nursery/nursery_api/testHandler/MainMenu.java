package com.nursery.nursery_api.testHandler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;

@Component
public class MainMenu extends ButtonHandler{
    String START_MESSAGE = "Выберите приют, который Вас интересует.";
    String[] buttonsName = {"Кошачий приют", "Собачий приют"};
    String[] callDataMain = {"-cats", "-dogs"};

    public MainMenu(ButtonComponent buttonComponent) {
        super(buttonComponent);
    }

    @Override
    public void handleCommand(String command) {
        if (command.equals("-main")) {
            sendBotMessageService.sendMessage(idChat.toString(), START_MESSAGE, buttonsName, callDataMain);
        }


    }
}
