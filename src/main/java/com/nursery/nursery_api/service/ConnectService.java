package com.nursery.nursery_api.service;

import com.nursery.nursery_api.dto.PostMessagePerson;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

@Service
public class ConnectService {
    private final Logger log = LoggerFactory.getLogger(ConnectService.class);

    private final TelegramBot telegramBot;

    private final VolunteerRepository volunteerRepository;

    private final DataReportRepository dataReportRepository;

    public ConnectService(TelegramBot telegramBot, VolunteerRepository volunteerRepository, DataReportRepository dataReportRepository) {
        this.telegramBot = telegramBot;
        this.volunteerRepository = volunteerRepository;
        this.dataReportRepository = dataReportRepository;
    }

    /**
     * Очередь из сущностей DataReport
     * todo если до 00:00 в очереди остались отчеты их нужно сохранить и перенести на следующий день как-то
     */
    private final ArrayBlockingQueue<DataReport> dataReportQueue = new ArrayBlockingQueue<DataReport>(200);

    /**
     * Успешно сданные отчеты.Это лишняя проверка, вдруг в промежутке из-за
     * непродуманной многопоточности, что-то пойдет не так.
     */
    private final Set<DataReport> doneReport = new HashSet<>();
    /**
     * Успешно сданные отчеты. Хранилище нужно для обновления основной очереди.
     * Так как если отчет не изменился и не поправился никак, то значит его не нужно добавлять.
     */
    private final Set<DataReport> badReport = new HashSet<>();

    /**
     * key - Volunteer
     * value - chat id human needy
     */
    private final Map<Volunteer, Long> volunteersList = new ConcurrentHashMap<>();

    /**
     * contain all chat id (volunteer and human needy)
     */
    private final Set<Long> dialogs = new ConcurrentSkipListSet<>();

    /**
     * Object with id chat Person and message for Volunteer
     */
    private final Queue<PostMessagePerson> queueMessage = new ArrayDeque<>();

    /**
     * Записываем из базы всех волонтеров в оперативную память
     * Так как у них нет пока собеседника ставим в значение null
     */
    @PostConstruct
    private void init() {
        List<Volunteer> allVolunteers = volunteerRepository.findAll();
        for (var element : allVolunteers) {
            volunteersList.put(element, 0L);
        }
    }

    /**
     * @param chatId
     * @return Проверка пользователя, присутствует ли он в активной беседе.
     */
    public boolean containInActiveDialog(Long chatId) {
        return dialogs.contains(chatId);
    }

    /**
     * @param postMessagePerson - сообщение от пользователя помещается в очередь.
     *                          В этом объекте есть idChat человека и поле с вопросом.
     */
    public void addQueueMessage(PostMessagePerson postMessagePerson) {
        if (postMessagePerson != null) {
            queueMessage.add(postMessagePerson);
        }
    }

    /**
     * Проверяем принадлежит ли текущий чат пользователю. Если нет, значит это волонтер.
     * Этот метод вызывается на том участке кода, где пользователь точно находиться в volunteersList
     * А значит идет активная беседа
     *
     * @param chatId
     * @return
     */
    public boolean isPerson(Long chatId) {
        return volunteersList.containsValue(chatId);
    }

    /**
     * Каждый 5 секунд проверяем есть ли свободные волонтеры и есть ли очередь из вопросов
     * Если есть то запускаем метод EveryoneWork, который занимает всех свободных волонтеров работой
     */
    @Scheduled(initialDelay = 2000, fixedRate = 5000)
    public void manageVolunteerAndPerson() {
        if (!queueMessage.isEmpty() && freeVolunteers()) {
            EveryoneWork(volunteersList, queueMessage);
        }
    }

    /**
     * @param volunteersList - все волонтеры сидящие в оперативной памяти
     * @param queueMessage   - очередь от вопрошающих
     *                       Суть метода - занять всех свободных волонтеров из очереди с вопросами от людей
     */
    private void EveryoneWork(Map<Volunteer, Long> volunteersList, Queue<PostMessagePerson> queueMessage) {

        for (Map.Entry<Volunteer, Long> entry : volunteersList.entrySet()) {
            // если текущий волонтер свободен и если очередь не пуста, запускаем коннект!
            if (!entry.getKey().isBusy() && queueMessage.peek() != null) {
                PostMessagePerson postMessagePerson = queueMessage.poll();
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


    // поставить private!!!

    /**
     * Проверка на существование хотя бы одного свободного волонтера
     *
     * @return
     */
    public boolean freeVolunteers() {
        return volunteersList.keySet().stream().anyMatch(volunteer -> !volunteer.isBusy());
    }

    /**
     * Связываем вопрошающего со свободным волонтером
     * метод входит в состав метода EveryoneWork, который обновляется по шедулеру
     *
     * @param chatId
     */
    public void addToDialogsUser(Long chatId) {
        Optional<Volunteer> volunteer = volunteersList.keySet().stream().filter(e -> !e.isBusy()).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(true);
            volunteersList.put(volunteer.get(), chatId);
            dialogs.add(chatId);
            dialogs.add(volunteer.get().getVolunteerChatId());
        }
    }

    /**
     * волонтер пишет <b>"Конец"</b>, а значит будет disconnect
     *
     * @param chatIdVolunteer - чат волонтера, который инициирует завершение беседы
     */
    public void disconnect(Long chatIdVolunteer) {
        Optional<Volunteer> volunteer = getVolunteerByChatId(chatIdVolunteer);
        if (volunteer.isPresent()) {
            // удаляем из диалогов и волонтера и вопрошающего
            dialogs.remove(volunteersList.get(volunteer.get()));
            dialogs.remove(volunteer.get().getVolunteerChatId());
            // освобождаем волонтера
            volunteer.get().setBusy(false);
        } else throw new NoSuchElementException("В журнале такого волонтера нет!");
    }

    /**
     * @param chatIdVolunteer Волонтер заканчивает свою смену
     *                        он пишет в чат бот "Стоп работа"
     *                        Его занятость становиться на true
     */
    public void stopWorkVolunteer(Long chatIdVolunteer) {
        // делаем проверку нет ли волонтера в активной беседе
        // если есть, то чистим ее
        if (dialogs.contains(chatIdVolunteer)) {
            disconnect(chatIdVolunteer);
        }
        // ставим волонтеру занятость на true
        Optional<Volunteer> volunteer = volunteersList.keySet().stream().filter(e -> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        volunteer.ifPresent(volunteerFree -> volunteerFree.setBusy(true));
    }


    /**
     * Добавляем в работу нового волонтера
     * пока не принял решение как он будет туда заноситься
     * Пусть человек пишет в чат <b>"Хочу стать волонтером"</b>
     * Сохраняется тут же в БД и выходит на линию, но имеет по умолчания занятость true
     * Чтобы начать работу ему нужно написать <b>"Работать"</b>
     *
     * @param volunteer
     * @return
     */
    public Volunteer addNewVolunteer(Volunteer volunteer) {
        if (volunteer != null) {
            // заносим товарища в базу
            volunteerRepository.save(volunteer);
            // И кидаем его сразу в оперативную память
            volunteersList.put(volunteer, 0L);
            return volunteer;

        } else throw new NoSuchElementException("Волонтер = null!");
    }

    /**
     * Волонтеру захотелось уйти совсем из проекта.
     * Его воля, пусть пишет <b>"Я ухожу"</b>
     * Он выпиливается, как из оперативной памяти, так и из БД
     *
     * @param idChatVolunteer
     * @return
     */
    public Volunteer iAmGonnaWayVolunteer(Long idChatVolunteer) {
        Optional<Volunteer> volunteer = volunteerRepository.findByVolunteerChatId(idChatVolunteer);
        if (volunteer.isPresent()) {
            volunteerRepository.delete(volunteer.get());
            // чистим общий чат
            disconnect(volunteer.get().getVolunteerChatId());
            // удаляем из мапы
            volunteersList.remove(volunteer.get());
        } else throw new NoSuchElementException("Волонтера с таким именем нет!");

        return volunteer.get();
    }


    /**
     * Если волонтер хочет поработать пусть вводит: <b>"Работать"</b>
     * Когда волонтер выходит на смену, идем базу достаем от туда волонтера и добавляем в оперативную память
     * Но если он уже есть в оперативной памяти, то просто ставим занятость на false
     *
     * @param chatIdVolunteer - чат волонтера, который хочет поработать
     * @return
     */
    public Volunteer iWantWorkVolunteer(Long chatIdVolunteer) {
        Optional<Volunteer> volunteer = volunteersList.keySet().stream().filter(e -> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(false);
            return volunteer.get();
        } else {
            Optional<Volunteer> volunteerFromDB = volunteerRepository.findByVolunteerChatId(chatIdVolunteer);
            if (volunteerFromDB.isPresent()) {
                volunteerFromDB.get().setBusy(false);
                volunteersList.put(volunteerFromDB.get(), 0L);
                return volunteerFromDB.get();
            } else throw new NoSuchElementException("Вас нет в базе данных! Обратитесь к администратору");
        }
    }

    /**
     * @param chatIdVolunteer
     * @return Найти волонтера по его чату из оперативной памяти
     */
    private Optional<Volunteer> getVolunteerByChatId(Long chatIdVolunteer) {
        return volunteersList.
                keySet().
                stream().
                filter(e -> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).
                findFirst();
    }

    /**
     * @param chatIdPerson чат вопрошающего
     * @return chat id волонтера
     * Суть: По чату вопрошающего найти id chat волонтера, который сейчас с ним общаеться
     */
    public Long getVolunteerChatIdByPersonChatId(Long chatIdPerson) {
        return volunteersList.
                entrySet().
                stream().
                filter(element -> Objects.equals(element.getValue(), chatIdPerson)).
                mapToLong(element -> element.getKey().getVolunteerChatId()).
                findFirst().orElse(0L);
    }

    /**
     * Возвращаем чат пользователя, что привязан в текущей сессии к консультанту
     *
     * @param chatIdVolunteer
     * @return
     */
    public Long getPersonChatIdByChatIdVolunteer(Long chatIdVolunteer) {
        Volunteer volunteer = volunteersList.keySet().stream().filter(e -> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst().get();
        return volunteersList.get(volunteer);
    }

    ////////////////////////////////////////////////////////////////////////////////////

    /**
     * Проверяем находится ли волонтер в режиме проверки отчетов.
     * Можно добавить команду для волонтера <b>"Отчеты?"</b>
     * И выдавать ему соответствующе сообщения
     * @param chatId -
     * @return
     */
    public boolean isReportVolunteer(Long chatId) {
        return volunteersList
                .keySet()
                .stream()
                .anyMatch(e -> Objects.equals(e.getVolunteerChatId(), chatId) && volunteersList.get(e) == 1L);
    }

    /**
     * в 21:00 собирается мапа из непроверенных отчетов
     * Если есть то запускаем метод EveryoneWork, который занимает всех свободных волонтеров работой
     * Нужно понять какое выражение прописать, чтобы с 21:00 и потом каждые полчаса обновлять очередь
     */
    @Scheduled(cron = "0 0 21 * * *")
    private void createDataReportList() {
        dataReportQueue.addAll(dataReportRepository.findReportForCheck());
    }

    /**
     * Проверяем обновленный список отчета с черным списком и с текущей очередью
     * Если есть совпадения по ключам, то нужно проверить значения. Если они разнятся, старое удаляем, новое привносим
     * todo Тут либо каждому дать право по фразе "Обновить", либо какой-то хитрый cron написать.
     */
    private synchronized void refreshDataReportQueue() {
        List<DataReport> reportsForCheck = dataReportRepository.findReportForCheck();
        for (var newDataReport : reportsForCheck) {
            if (badReport.contains(newDataReport)) {
                DataReport oldVersion = badReport.stream().filter(e -> e.equals(newDataReport)).findFirst().get();
                if (!compareDataReport(newDataReport, oldVersion)) {
                    badReport.remove(oldVersion);
                    badReport.add(newDataReport);
                }
                continue;
            }

            if (dataReportQueue.contains(newDataReport)) {
                dataReportQueue.remove(newDataReport);
                dataReportQueue.add(newDataReport);
            } else {
                dataReportQueue.add(newDataReport);
            }
        }
    }

    /**
     * Проверяет два отчета на наличие разницы
     * @param newVersion
     * @param oldVersion
     * @return
     */
    private boolean compareDataReport(DataReport newVersion, DataReport oldVersion) {
        return
                newVersion.getFileSize().equals(oldVersion.getFileSize()) &&
                        newVersion.getMessagePerson().equals(oldVersion.getMessagePerson());

    }

    /**
     * Волонтер написав <b>"Проверка отчетов"</b> входит в режим проверки отчетов
     * Костыль - присвоить ему занятость на true, а значение поставить 1L
     * Так как такого чата никогда не будет существовать, то это будет маркером
     * Что волонтер работает с отчетами
     * <p>
     * Возвращаем пустой объект в ответ на пустой отчет,
     * нужно слать волонтеру ответ <i>"Отчетов пока нет или вы не находитесь в режиме проверки отчетов.
     * Введите <b>"Cтатистика"</b> - чтобы получить информацию об очереди отчетов или <b>"Проверить отчеты"</b> - чтобы стать участником проверки</i>
     *
     * @return
     */
    public synchronized DataReport getOneDataReport() {
        Optional<Volunteer> reportVolunteer = volunteersList.keySet().stream().filter(e -> volunteersList.get(e) == 1L).findFirst();
        if (!dataReportQueue.isEmpty() && reportVolunteer.isPresent()) {
            return dataReportQueue.poll();
        }

        return new DataReport();
    }

    /**
     * Выводиться статистика по волонтерам. Волонтеру нужно ввести команду <b>"Статистика"</b>
     * Косяк в том, что на текущий момент распознать отдыхающего и находящегося в работе консультанта, пока нельзя.
     * @return
     */
    public String statistic(){
        StringBuilder result=new StringBuilder();
        int amountConsultVolunteer= (int) volunteersList.keySet().stream().filter(e-> !e.isBusy()).count();  // свободные
        int amountRestOrWorkVolunteer= (int) volunteersList.keySet().stream().filter(Volunteer::isBusy).count();     // на отдыхе или работе
        int amountReportMode= (int) volunteersList.keySet().stream().filter(e->volunteersList.get(e)==1L).count();
        result.append("Готовые к консультации: ").
                append(amountConsultVolunteer).
                append("\n На отдыхе или в активной работе: ").
                append(amountRestOrWorkVolunteer).
                append("\n Отчеты проверяют: ").
                append(amountReportMode);
        return result.toString();
    }

    /**
     * Волонтер переходит в режим проверки отчетов. Для этого ему нужно ввести <b>"Проверить отчеты"</b>
     * Перед этим желательно сделать <b>"Стоп работа"</b>, чтобы никто не смог вклиниться с консультацией
     *
     * @param idChatVolunteer
     */
    public void reportModeActive(Long idChatVolunteer) {
        for (Map.Entry<Volunteer, Long> volunteer : volunteersList.entrySet()) {
            if (Objects.equals(volunteer.getKey().getVolunteerChatId(), idChatVolunteer)) {
                volunteer.getKey().setBusy(true);
                volunteer.setValue(1L);
                break;
            }
        }
    }

    /**
     * Волонтер выходит из этого режима
     * 1 мы меняем на 0
     *
     * @param idChatVolunteer
     */
    //todo рефактор, код повторяет reportModeActive
    public void reportModeDisable(Long idChatVolunteer) {
        for (Map.Entry<Volunteer, Long> volunteer : volunteersList.entrySet()) {
            if (Objects.equals(volunteer.getKey().getVolunteerChatId(), idChatVolunteer)) {
                volunteer.getKey().setBusy(true);
                volunteer.setValue(0L);
                break;
            }
        }
    }

    /**
     * @param dataReport - отчет, который удовлетворил Волонтера
     * @return Сохраняем в базу отчет
     */
    public synchronized DataReport reportIsDoneSaveToBd(DataReport dataReport) {
        if (!doneReport.contains(dataReport)) {
            doneReport.add(dataReport);
            dataReportRepository.save(dataReport);
            log.info("Отчет: {} дата: {} успешно обработан", dataReport.getReport().getIdReport(), dataReport.getDateReport());
            return dataReport;
        }
        log.debug("Отчет: {} дата: {} уже был обработан!", dataReport.getReport().getIdReport(), dataReport.getDateReport());
        return dataReport;
    }

    /**
     * Помещаем отчет в черный список (он удаляется в полночь)
     * Нужно для того, чтобы при обновлении списка проверять, а стоит ли добавлять отчет в очередь
     * Может человек так и не отредактировал свой отчет.
     *
     * @param dataReport - плохой отчет.
     * @return
     */
    public synchronized DataReport reportIsBadNotSaveToBd(DataReport dataReport) {
        if (!badReport.contains(dataReport)) {
            doneReport.add(dataReport);
            log.info("Плохой отчет: {} дата: {} занесен в черный список", dataReport.getReport().getIdReport(), dataReport.getDateReport());
            return dataReport;
        }
        log.debug("Отвергнутый отчет: {} дата: {} уже был обработан!", dataReport.getReport().getIdReport(), dataReport.getDateReport());
        return dataReport;
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

    public void removeAllListVolunteers() {
        volunteersList.clear();
    }
}
