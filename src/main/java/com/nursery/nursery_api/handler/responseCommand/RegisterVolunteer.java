package com.nursery.nursery_api.handler.responseCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.RegisterHandler;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.NurseryDBService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class RegisterVolunteer implements RegisterHandler {

    private final VolunteerRepository volunteerRepository;
    String inputMessage;

    public RegisterVolunteer(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }


    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService) {
        String response = "Вы завершили регистрацию. Теперь вы волонтер";

        String nameVolunteer = inputMessage.substring(inputMessage.lastIndexOf("|") + 1);

        Volunteer volunteer=volunteerRepository.findByName(nameVolunteer);

        if (volunteer!=null) {
            volunteer.setVolunteerChatId(idChat);
            volunteerRepository.save(volunteer);
        } else response="Ошибка. Ваше имя не смогли найти в базе данных. Проверьте правильность написания. Пример: -regVal|Анастасия Забродина";

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text(response).
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     *
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        if (inputMessage.startsWith("-regVol|")) {
            this.inputMessage = inputMessage;
            return true;
        }
        return false;
    }
}
