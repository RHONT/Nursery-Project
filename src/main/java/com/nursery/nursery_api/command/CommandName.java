package com.nursery.nursery_api.command;

public enum CommandName {
    START("start"),
    CAT("cat"),
    DOG("dog"),
    VOLUNTEER("volunteer"),
    INFO("info"),
    ADOPTCAT("catAdopt"),
    ADOPTDOG("dogAdopt"),
    REPORT("report");

    private final String commandName;

    CommandName(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
