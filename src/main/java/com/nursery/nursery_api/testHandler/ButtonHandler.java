package com.nursery.nursery_api.testHandler;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;


public abstract class ButtonHandler {

    protected final ButtonComponent buttonComponent;

    protected final TelegramBot bot;
    protected final NurseryDBService nurseryDBService;
    protected final SendBotMessageService sendBotMessageService;

    protected Long idChat=0L;
    protected ButtonHandler next;

    public ButtonHandler(ButtonComponent buttonComponent) {
        this.buttonComponent = buttonComponent;
        bot=buttonComponent.getBot();
        nurseryDBService=buttonComponent.getNurseryDBService();
        sendBotMessageService= buttonComponent.sendBotMessageService;

    }

    public void setNext(ButtonHandler next) {
        this.next = next;
    }

    public abstract void handleCommand(String command);

    public void setIdChat(Long idChat) {
        this.idChat = idChat;
    }

//    @Override
//    public void handle(Long idChat,,,) {
//
//        sendBotMessageService.sendMessage(idChat.toString(), ADOPT_MESSAGE, buttonsNameForCat, callDataCat);
//    }

}
