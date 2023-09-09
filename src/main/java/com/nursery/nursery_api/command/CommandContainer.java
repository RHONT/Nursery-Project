package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;

import java.util.HashMap;
import java.util.Map;

import static com.nursery.nursery_api.command.CommandName.*;

public class CommandContainer {

    private final Map<String, Command> commandMap = new HashMap<>();

    public CommandContainer(SendBotMessageService sendBotMessageService) {

        commandMap.put(START.getCommandName(), new StartCommand(sendBotMessageService));
        commandMap.put(CAT.getCommandName(), new CatAndDogCommand(sendBotMessageService));
        commandMap.put(DOG.getCommandName(), new CatAndDogCommand(sendBotMessageService));
        commandMap.put(VOLUNTEER.getCommandName(), new VolunteerCommand(sendBotMessageService));
        commandMap.put(INFO.getCommandName(), new InfoCommand(sendBotMessageService));
        commandMap.put(ADOPTCAT.getCommandName(), new AdoptCommand(sendBotMessageService));
        commandMap.put(ADOPTDOG.getCommandName(), new AdoptCommand(sendBotMessageService));
        commandMap.put(REPORT.getCommandName(), new ReportCommand(sendBotMessageService));

    }

    public Command retrieveCommand(String commandIdentifier) {
        return commandMap.get(commandIdentifier);
    }
}
