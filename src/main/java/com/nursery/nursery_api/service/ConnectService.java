package com.nursery.nursery_api.service;

import com.nursery.nursery_api.SomeClasses.PostMessagePerson;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ConnectService {
    private final TelegramBot telegramBot;

    private final VolunteerRepository volunteerRepository;

    public ConnectService(TelegramBot telegramBot, VolunteerRepository volunteerRepository) {
        this.telegramBot = telegramBot;
        this.volunteerRepository = volunteerRepository;
    }

//    public void setVolunteerRepository(VolunteerRepository volunteerRepository) {
//        this.volunteerRepository = volunteerRepository;
//    }

    /**
     * key - Volunteer
     * value - chat id human needy
     */
    private final Map<Volunteer,Long> volunteersList =new ConcurrentHashMap<>();

    /**
     * contain all chat id (volunteer and human needy)
     */
    private final Set<Long> dialogs=new ConcurrentSkipListSet<>();

    /**
     * Object with id chat Person and message for Volunteer
     */
    private final Queue<PostMessagePerson> queueMessage=new ArrayDeque<>();

    /**
     * Записываем из базы всех волонтеров в оперативную память
     * Так как у них нет пока собеседника ставим в значение null
     */
    @PostConstruct
    private void init(){
        List<Volunteer> allVolunteers = volunteerRepository.findAll();
        for (var element:allVolunteers) {
            volunteersList.put(element,0L);
        }
    }

    /**
     *
     * @param chatId
     * @return
     * Проверка пользователя, присутствует ли он в активной беседе.
     */
    public boolean containInActiveDialog(Long chatId){
        return  dialogs.contains(chatId);
    }

    /**
     * @param postMessagePerson - сообщение от пользователя помещается в очередь.
     *                          В этом объекте есть idChat человека и поле с вопросом.
     */
    public void addQueueMessage(PostMessagePerson postMessagePerson){
        if (postMessagePerson!=null) {
            queueMessage.add(postMessagePerson);
        }
    }

    public boolean isPerson(Long chatId){
        return volunteersList.containsValue(chatId);
    }

    /**
     * Каждый 5 секунд проверяем есть ли свободные волонтеры и есть ли очередь из вопросов
     * Если есть то запускаем метод EveryoneWork, который занимает всех свободных волонтеров работой
     */
    @Scheduled(initialDelay = 2000, fixedRate = 5000)
    public void manageVolunteerAndPerson(){
        if (!queueMessage.isEmpty() && freeVolunteers(volunteersList)) {
            EveryoneWork(volunteersList,queueMessage);
        }
    }

    /**
     *
     * @param volunteersList - все волонтеры сидящие в оперативной памяти
     * @param queueMessage - очередь от вопрошающих
     * Суть метода - занять всех свободных волонтеров из очереди с вопросами от людей
     */
    private void EveryoneWork(Map<Volunteer, Long> volunteersList, Queue<PostMessagePerson> queueMessage) {

        for (Map.Entry<Volunteer,Long> entry:volunteersList.entrySet()) {
            // если текущий волонтер свободен и если очередь не пуста, запускаем коннект!
            if (!entry.getKey().isBusy() && queueMessage.peek()!=null) {
                    PostMessagePerson postMessagePerson=queueMessage.poll();
                    addToDialogsUser(postMessagePerson.getChatIdPerson());

                    try {
                        telegramBot.execute(SendMessage.
                                builder().
                                chatId(entry.getKey().getVolunteerChatId()).
                                text(postMessagePerson.getMessage()).
                                build());
                    } catch (TelegramApiException e) {
                        e.printStackTrace();
                    }
                    // если условия эти не выполняются, прекращаем цикл
            } else break;
        }

    }

    /**
     *
     * @param volunteersList - список волонтеров в оперативной памяти
     * @return - если хотя бы один свободен возвращаем true
     * Проверяем есть ли свободные волонтеры в оперативной памяти
     */

    // поставить private!!!
    public boolean freeVolunteers(Map<Volunteer, Long> volunteersList) {
        return volunteersList.keySet().stream().anyMatch(volunteer-> !volunteer.isBusy());
    }

    // связываем свободного волонтера со страждущим человеком
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

    /**
     * @param chatIdVolunteer - чат волонтера, который инициирует завершение беседы
     */
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

    /**
     *
     * @param chatIdVolunteer
     * Волонтер заканчивает свою смену
     * он пишет в чат бот "Стоп работа"
     * Его занятость становиться на true
     */
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
            // И кидаем его сразу в оперативную память
            volunteersList.put(volunteer,0L);
            return volunteer;

        } else throw new NoSuchElementException("Волонтер = null!");
    }

    // Если волонтер хочет самовыпилиться
    // например пишет в чат "- Не хочу быть волонтером @GonzaMy"
    // Нужно прописать проверку в телеграмме на строку формата message.starWith("- Не хочу быть волонтером")
    // и вытащить от туда @GonzaMy
    public Volunteer hasLeftVolunteer(Long idChatVolunteer){
        Optional<Volunteer> volunteer=volunteerRepository.findByVolunteerChatId(idChatVolunteer);
        if (volunteer.isPresent()) {
            volunteerRepository.delete(volunteer.get());
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

    /**
     *
     * @param chatIdVolunteer
     * @return
     * Найти волонтера по его чату из оперативной памяти
     */
    private Optional<Volunteer> getVolunteerByChatId(Long chatIdVolunteer){
        return volunteersList.
                keySet().
                stream().
                filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).
                findFirst();
    }

    /**
     * @param chatIdPerson чат вопрошающего
     * @return chat id волонтера
     * Суть: По чату вопрошающего найти id chat волонтера, который сейчас с ним общаеться
     */
    public Long getVolunteerChatIdByPersonChatId(Long chatIdPerson){
        return volunteersList.
                entrySet().
                stream().
                filter(element-> Objects.equals(element.getValue(), chatIdPerson)).
                mapToLong(element->element.getKey().getVolunteerChatId()).
                findFirst().orElse(0L);
    }

    public Long getPersonChatIdByChatIdVolunteer(Long chatIdVolunteer){
        Volunteer volunteer = volunteersList.keySet().stream().filter(e -> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst().get();
        return volunteersList.get(volunteer);

    }

    // Для тестов

    public Map<Volunteer, Long> getVolunteersList() {
        return volunteersList;
    }

    public Set<Long> getDialogs() {
        return dialogs;
    }

    public Queue<PostMessagePerson> getQueueMessage() {
        return queueMessage;
    }

    public void removeAllListVolunteers(){
        volunteersList.clear();
    }
}
