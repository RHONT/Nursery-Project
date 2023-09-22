package com.nursery.nursery_api.handler.responseCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.handler.RegisterHandler;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.repositiry.NurseryRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Optional;

@Component
public class RegisterPerson implements RegisterHandler {
    private final NurseryRepository nurseryRepository;
    private final PersonRepository personRepository;
    String inputMessage;

    public RegisterPerson(NurseryRepository nurseryRepository, PersonRepository personRepository) {
        this.nurseryRepository = nurseryRepository;
        this.personRepository = personRepository;
    }

    @Override
    public void handle(Long idChat, TelegramBot bot, NurseryDBService nurseryDBService) {
        String response = "Вы завершили регистрацию";
        String namePerson = inputMessage.substring(inputMessage.lastIndexOf("|") + 1);

        Long idNursery = nurseryRepository.findNurseryByNameNursery(nurseryDBService.getVisitors().get(idChat)).getIdNursery();

        Optional<Person> person = personRepository.findPersonByNamePersonAndByIdNursery(namePerson, idNursery);

        if (person.isPresent()) {
            person.get().setIdChat(idChat);
            personRepository.save(person.get());
        } else
            response = "Вы не были найдены в базе. Перед регистрацией выберите в основном меню ваш питомник и затем " +
                    "повторите попытку. Проверьте правильность написания запроса. Пример:-regPerson|Анастасия Забродина  " +
                    "Если не удаеться пройти регистрацию обратитесь к волонтеру ";

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
        if (inputMessage.startsWith("-regPerson|")) {
            this.inputMessage = inputMessage;
            return true;
        }
        return false;
    }
}
