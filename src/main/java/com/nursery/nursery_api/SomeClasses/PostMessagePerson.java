package com.nursery.nursery_api.SomeClasses;

public class PostMessagePerson {
    private final Long chatIdPerson;
    private String message;

    public PostMessagePerson(Long chatIdPerson) {
        this.chatIdPerson = chatIdPerson;
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
