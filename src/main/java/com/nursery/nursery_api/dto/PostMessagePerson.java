package com.nursery.nursery_api.dto;

public class PostMessagePerson {
    private final Long chatIdPerson;
    private String message;

    public PostMessagePerson(Long chatIdPerson, String messageUser) {
        this.chatIdPerson = chatIdPerson;
        this.message=messageUser;
    }

    public Long getChatIdPerson() {
        return chatIdPerson;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
