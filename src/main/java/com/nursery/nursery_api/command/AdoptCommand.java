package com.nursery.nursery_api.command;

import com.nursery.nursery_api.service.SendBotMessageService;
import org.telegram.telegrambots.meta.api.objects.Update;

public class AdoptCommand implements Command {
    private final SendBotMessageService sendBotMessageService;

    public final static String ADOPT_MESSAGE = "Hello! Here you can find information on how to adopt " +
            "a pet.";
    String[] buttonsNameForCat = {"Rules for getting to know a cat before adoption",
            "Documents for adoption", "Recommendation for transportation of a cat",
            "Recommendations for home improvement for a kitten",
            "Recommendations for home improvement for an adult cat",
            "Recommendations for home improvement for a disabled cat",
            "A list of reasons for refusal",
            "Leave your contact information",
            "Send a message to a volunteer"};

    String[] callDataCat = {"knowCat",
            "docsCat", "transportationCat",
            "kittenCat",
            "adultCat",
            "disabledCat",
            "refusalCat",
            "contactCat",
            "volunteer"};
    String[] buttonsNameForDog = {"Rules for getting to know a dog before adoption",
            "Documents for adoption", "Recommendation for transportation of a dog",
            "Recommendations for home improvement for a puppy",
            "Recommendations for home improvement for an adult dog",
            "Recommendations for home improvement for a disabled dog",
            "Cynologist advice on the initial communication with the dog",
            "A list of checked cynologists",
            "A list of reasons for refusal",
            "Leave your contact information",
            "Send a message to a volunteer"};

    String[] callDataDog = {"knowDog",
            "docsDog", "transportationDog",
            "puppyDog",
            "adultDog",
            "disabledDog",
            "cynologistDog",
            "cynologustsDog",
            "refusalDog",
            "contactDog",
            "volunteer"};

    public AdoptCommand(SendBotMessageService sendBotMessageService) {
        this.sendBotMessageService = sendBotMessageService;
    }

    @Override
    public void execute(Update update) {
        if (update.getCallbackQuery().getData().equals("catAdopt")) {
            sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                    ADOPT_MESSAGE, buttonsNameForCat, callDataCat);
        } else if (update.getCallbackQuery().getData().equals("dogAdopt")) {
            sendBotMessageService.sendMessage(update.getCallbackQuery().getMessage().getChatId().toString(),
                    ADOPT_MESSAGE, buttonsNameForDog, callDataDog);
        }
    }
}
