package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;


@Service
public class ReportService {
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final DataReportRepository dataReportRepository;
    private final ConnectService connectService;

    public ReportService(DataReportRepository dataReportRepository, ConnectService connectService) {
        this.dataReportRepository = dataReportRepository;
        this.connectService = connectService;
        volunteersList=connectService.getVolunteersList();
    }

    /**
     * Журнал который содержит тех, кто вознамерился отправить отчет.
     */
    private final Set<Long> reportJournal=new ConcurrentSkipListSet<>(Set.of(1L));

    /**
     * Добавляем человека в спискок желающих отправить отчет
     * @param chatIdPersonReport
     */
    public void addNewPersonForReport(Long chatIdPersonReport){
        reportJournal.add(chatIdPersonReport);
    }

    /**
     * Удаляем человека из списка готовых отправить отчет
     * @param chatIdPersonReport
     */
    public void deletePersonForReport(Long chatIdPersonReport){
        reportJournal.remove(chatIdPersonReport);
    }

    public boolean containPersonForReport(Long chatIdPerson){
        return reportJournal.contains(chatIdPerson);
    }


    /**
     * key - Volunteer
     * value - chat id human needy
     */
    private Map<Volunteer, Long> volunteersList=null;

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


}