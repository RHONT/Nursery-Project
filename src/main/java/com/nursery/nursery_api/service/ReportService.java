package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.nursery.nursery_api.Global.GlobalVariable.volunteersList;


@Service
public class ReportService {
    private final Logger log = LoggerFactory.getLogger(ReportService.class);

    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;

    public ReportService(DataReportRepository dataReportRepository, ReportRepository reportRepository) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
    }


    /**
     * Журнал который содержит тех, кто вознамерился отправить отчет.
     */
    private Set<Long> reportJournal=new ConcurrentSkipListSet<>();

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
     * Проверяем находится ли волонтер в режиме проверки отчетов.
     * Можно добавить команду для волонтера <b>"Отчеты?"</b>
     * И выдавать ему соответствующе сообщения
     * Это нужно для сомого волонтера. Чтобы он понимал, находиться ли он в этом режиме.
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
    //todo закрыть метод, открывал только ради тестов
    @Scheduled(cron = "0 0 21 * * *")
    public void createDataReportList() {
        List<DataReport> reportForCheck = dataReportRepository.findReportForCheck();
        reportForCheck.removeIf(dataReport->dataReport.getFileSize()==null || dataReport.getMessagePerson()==null) ;
        dataReportQueue.addAll(reportForCheck);
    }

    /**
     * Проверяем обновленный список отчета с черным списком и с текущей очередью
     * Если есть совпадения по ключам, то нужно проверить значения. Если они разнятся, старое удаляем, новое привносим
     * todo Тут либо каждому дать право по фразе "Обновить", либо какой-то хитрый cron написать.
     */
    private synchronized void refreshDataReportQueue() {
        List<DataReport> reportsForCheck = dataReportRepository.findReportForCheck();
        reportsForCheck.removeIf(dataReport->dataReport.getMessagePerson()==null || dataReport.getFileSize()==0);
        for (var newDataReport : reportsForCheck) {
            if (badReport.contains(newDataReport)) {
                DataReport oldVersion = badReport.stream().filter(e -> e.equals(newDataReport)).findFirst().get();
                if (!compareDataReport(newDataReport, oldVersion)) {
                    badReport.remove(oldVersion);
                } else continue;

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
            badReport.add(dataReport);
            log.info("Плохой отчет: {} дата: {} занесен в черный список", dataReport.getReport().getIdReport(), dataReport.getDateReport());
            return dataReport;
        }
        log.debug("Отвергнутый отчет: {} дата: {} уже был обработан!", dataReport.getReport().getIdReport(), dataReport.getDateReport());
        return dataReport;
    }


    /**
     * Method create new object Report attached to Person
     * @param report
     * @return Report
     */
    public Report addNewReportForPerson (Report report){
        return reportRepository.save (report);
    }
    /**
     * Method search Report that attach to Person.
     * @param person
     * @return Report
     */
    public Report findReportInfoForPerson (Person person){
        return reportRepository.findReportByPersonId(person.getIdPerson());
    }

    /**
     * Method search Report (common entity for DataReport) by DataReport
     * @param dataReport
     * @return Report
     */
    public Report findReportByDataReport (DataReport dataReport){
        return dataReport.getReport();
    }

    /**
     * Method return List of all Report entity.
     * @return List<Report>
     */
    public List<Report> findAll (){
        return reportRepository.findAll();
    }
    /**
     * Method edit object Report, save it and return new version of Report.
     * @param report
     * @return Report
     */
    public Report editReport (Report report){
        return reportRepository.save(report);
    }

    public Report deleteReportByReportId(Long reportId){
        return reportRepository.deleteReportByIdReport(reportId);
    }

    public ArrayBlockingQueue<DataReport> getDataReportQueue() {
        return dataReportQueue;
    }

    public Set<DataReport> getBadReport() {
        return badReport;
    }
}
