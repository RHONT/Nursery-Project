package com.nursery.nursery_api.service;

public interface SendBotMessageService {

    void sendMessage(String chatId, String message, String[] buttonsName, String[] callData);
}
