package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ConnectService {

    private final VolunteerRepository volunteerRepository;

    public ConnectService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    /**
     * key - Volunteer
     * value - chat id human needy
     */
    private final Map<Volunteer,Long> volunteersList =new ConcurrentHashMap<>();

    /**
     * contain all chat id (volunteer and human needy)
     */
    private final Set<Long> dialogs=new ConcurrentSkipListSet<>();

    @PostConstruct
    private void init(){
        // Записываем из базы всех волонтеров в оперативную память
        // Так как у них нет пока собеседника ставим в значение null
        List<Volunteer> allVolunteers = volunteerRepository.findAll();
        for (var element:allVolunteers) {
            volunteersList.put(element,null);
        }
    }

    // связываем свободного волонтера с страждущим человеком
    public void addToDialogsUser(Long chatId){
        Optional<Volunteer> volunteer = volunteersList.keySet().stream().filter(e -> !e.isBusy()).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(true);
            volunteersList.put(volunteer.get(),chatId);
            dialogs.add(chatId);
            dialogs.add(volunteer.get().getVolunteerChatId());
        }
    }

    // волонтер пишет "-к", а значит беседу нужно закончить
    public void disconnect(Long chatIdVolunteer){
        Optional<Volunteer> volunteer=getVolunteerByChatId(chatIdVolunteer);
        if (volunteer.isPresent()) {
            // удаляем из диалогов и волонтера и вопрошающего
            dialogs.remove(volunteersList.get(volunteer.get()));
            dialogs.remove(volunteer.get().getVolunteerChatId());
            // освобождаем волонтера
            volunteer.get().setBusy(false);
        } else throw new NoSuchElementException("В журнале такого волонтера нет!");
    }

    // Если волонтер заканчивает свою смену
    // он пишет в чат бот "Стоп работа"
    public void disappearanceVolunteer(Long chatIdVolunteer){
        // делаем проверку нет ли волонтера в активной беседе
        // если есть, то чистим ее
        if (dialogs.contains(chatIdVolunteer)) {
            disconnect(chatIdVolunteer);
        }
        // ставим волонтеру занятость на true
        Optional<Volunteer> volunteer= volunteersList.keySet().stream().filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        volunteer.ifPresent(volunteerFree-> volunteerFree.setBusy(true));
    }

    // Добавляем в работу нового волонтера
    // пока не принял решение как он будет туда заноситься
    // Пусть будет "- Хочу стать волонтером: @GonzaMy"
    // Нужно прописать проверку в телеграмме на строку формата message.starWith("- Хочу стать волонтером:")
    // Регуляркой достать @GonzaMy и сохранить в базу нового пользователя

    public Volunteer addNewVolunteer(Volunteer volunteer){
        if (volunteer!=null) {
            // заносим товарища в базу
            volunteerRepository.save(volunteer);
            return volunteer;

        } else throw new NoSuchElementException("Волонтер = null!");
    }

    // Если волонтер хочет самовыпилиться
    // например пишет в чат "- Не хочу быть волонтером @GonzaMy"
    // Нужно прописать проверку в телеграмме на строку формата message.starWith("- Не хочу быть волонтером")
    // и вытащить от туда @GonzaMy
    public Volunteer hasLeftVolunteer(String telegramName){
        Optional<Volunteer> volunteer=volunteerRepository.findByTelegramName(telegramName);
        if (volunteer.isPresent()) {
            volunteerRepository.deleteByTelegramName(telegramName);
            // чистим общий чат
            disconnect(volunteer.get().getVolunteerChatId());
            // удаляем из мапы
            volunteersList.remove(volunteer.get());
        } else throw new NoSuchElementException("Волонтера с таким именем нет!");

        return volunteer.get();
    }

    // Когда волонтер выходит на смену, идем базу достаем от туда волонтера и добавляем в оперативную память
    // Но  если он уже есть в оперативной памяти, то просто ставим занятость на false
    // пусть он вводит: "Волонтер работа: @GonzaMe"
    // если нет возвращаем null
    public Volunteer goOnShiftVolunteer(Long chatIdVolunteer){
        Optional<Volunteer> volunteer= volunteersList.keySet().stream().filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(false);
            return volunteer.get();
        } else {
            Optional<Volunteer> volunteerFromDB=volunteerRepository.findByVolunteerChatId(chatIdVolunteer);
            if (volunteerFromDB.isPresent()) {
                volunteerFromDB.get().setBusy(false);
                volunteersList.put(volunteerFromDB.get(),null);
                return volunteerFromDB.get();
            } else throw new NoSuchElementException("Вас нет в базе данных! Обратитесь к администратору");
        }
    }

    private Optional<Volunteer> getVolunteerByChatId(Long chatIdVolunteer){
        return volunteersList.
                keySet().
                stream().
                filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).
                findFirst();
    }
}
