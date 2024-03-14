package com.nursery.nursery_api.service;

import com.nursery.nursery_api.dto.ReportDto;
import com.nursery.nursery_api.model.*;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.PetRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentSkipListSet;

import static com.nursery.nursery_api.global.GlobalVariable.volunteersList;


@Service
public class ReportService {
    private final Logger logger = LoggerFactory.getLogger(ReportService.class);

    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;
    private final PersonRepository personRepository;
    private final PetRepository petRepository;

    public ReportService(DataReportRepository dataReportRepository, ReportRepository reportRepository, PersonRepository personRepository, PetRepository petRepository) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.petRepository = petRepository;
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
        logger.info("Вызван метод addNewPersonForReport {}", chatIdPersonReport);
        reportJournal.add(chatIdPersonReport);
    }

    /**
     * Удаляем человека из списка готовых отправить отчет
     * @param chatIdPersonReport
     */
    public void deletePersonForReport(Long chatIdPersonReport){
        logger.info("Вызван метод deletePersonForReport с параметром {}.", chatIdPersonReport);
        reportJournal.remove(chatIdPersonReport);
    }

    public boolean containPersonForReport(Long chatIdPerson){
        logger.info("Вызван метод containPersonForReport с параметром {}.", chatIdPerson);
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
        logger.info("Вызван метод isReportVolunteer с параметром {}.", chatId);
        return volunteersList
                .keySet()
                .stream()
                .anyMatch(e -> Objects.equals(e.getVolunteerChatId(), chatId) && volunteersList.get(e) == 1L);
    }

    /**
     * Проверяем обновленный список отчета с черным списком и с текущей очередью
     * Если есть совпадения по ключам, то нужно проверить значения. Если они разнятся, старое удаляем, новое привносим
     * todo Тут либо каждому дать право по фразе "Обновить", либо какой-то хитрый cron написать.
     */
    public synchronized void refreshDataReportQueue() {

        logger.info("Вызван метод refreshDataReportQueue.");

        List<DataReport> reportsForCheck = dataReportRepository.findReportForCheck();

        reportsForCheck.removeIf(dataReport->dataReport.getMessagePerson()==null || dataReport.getFileSize()==null);

        for (var newDataReport : reportsForCheck) {
            if (badReport.contains(newDataReport)) {
                DataReport oldVersion = badReport.stream().filter(e -> e.equals(newDataReport)).findFirst().get();
                if (!compareDataReport(newDataReport, oldVersion)) {
                    badReport.remove(oldVersion);
                    dataReportQueue.add(newDataReport);
                }

            } else {
                if (dataReportQueue.contains(newDataReport)) {
                    dataReportQueue.remove(newDataReport);
                } else dataReportQueue.add(newDataReport);
            }

//            dataReportQueue.removeIf(dataReportQueue::contains);
//            dataReportQueue.add(newDataReport);
        }
    }

    /**
     * Проверяет два отчета на наличие разницы
     * @param newVersion
     * @param oldVersion
     * @return
     */
    private boolean compareDataReport(DataReport newVersion, DataReport oldVersion) {
        logger.info("Вызван метод compareDataReport.");
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
        logger.info("Вызван метод getOneDataReport.");
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
        logger.info("Вызван метод statistic.");
        StringBuilder result=new StringBuilder();
        int amountConsultVolunteer= (int) volunteersList.keySet().stream().filter(e-> !e.isBusy()).count();  // свободные
        int amountConsultNowVolunteer= (int) volunteersList.keySet().stream().filter(e-> e.isBusy() && volunteersList.get(e)>1L).count();
        int amountReportMode= (int) volunteersList.keySet().stream().filter(e->volunteersList.get(e)==1L).count();
        int amountAllReportForCheck=dataReportQueue.size();

        result.append("Готовые к консультации: ").
                append(amountConsultVolunteer).
                append("\nЗаняты консультациями: ").
                append(amountConsultNowVolunteer).
                append("\nОтчеты проверяют: ").
                append(amountReportMode).
                append("\nВсего отчетов для проверки: ").
                append(amountAllReportForCheck);
        return result.toString();
    }

    /**
     * Волонтер переходит в режим проверки отчетов. Для этого ему нужно ввести <b>"Проверить отчеты"</b>
     * Перед этим желательно сделать <b>"Стоп работа"</b>, чтобы никто не смог вклиниться с консультацией
     *
     * @param idChatVolunteer
     */
    public void reportModeActive(Long idChatVolunteer) {
        logger.info("Вызван метод reportModeActive с параметром {}.", idChatVolunteer);
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
        logger.info("Вызван метод reportModeDisable с параметром {}.", idChatVolunteer);
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
        logger.info("Вызван метод reportIsDoneSaveToBd.");
        if (!doneReport.contains(dataReport)) {
            doneReport.add(dataReport);
            dataReport.setCheckMessage(true);
            dataReportRepository.save(dataReport);
            badReport.remove(dataReport);
            logger.info("Отчет: {} дата: {} успешно обработан", dataReport.getReport().getIdReport(), dataReport.getDateReport());
            return dataReport;
        }
        logger.debug("Отчет: {} дата: {} уже был обработан!", dataReport.getReport().getIdReport(), dataReport.getDateReport());
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
            logger.info("Плохой отчет: {} дата: {} занесен в черный список", dataReport.getReport().getIdReport(), dataReport.getDateReport());
            return dataReport;
        }
        logger.debug("Отвергнутый отчет: {} дата: {} уже был обработан!", dataReport.getReport().getIdReport(), dataReport.getDateReport());
        return dataReport;
    }


    /**
     * Method create new object Report attached to Person
     * @return Report
     */
    public Report addNewReportForPerson (ReportDto reportDto){
        logger.info("Вызван метод addNewReportForPerson.");
       Optional<Person>  person=personRepository.findById(reportDto.getIdPerson());
       Optional<Pet> pet=petRepository.findById(reportDto.getIdPet());
        if (person.isPresent() && pet.isPresent()) {

            pet.get().setPerson(person.get());
            petRepository.save(pet.get());

            Report report = new Report();
            report.setPerson(person.get());
            report.setForteit(0L);
            report.setDayReport(30);

            return reportRepository.save (report);
        } else throw new IllegalArgumentException("Пользователь не найден");


    }
    /**
     * Method search Report that attach to Person.
     * @param personId
     * @return Report
     */
    public Report findReportInfoForPersonId (Long personId){
        logger.info("Вызван метод findReportInfoForPersonId с параметром {}.", personId);
        return reportRepository.findReportByPersonId(personId);
    }

    /**
     * Method search Report (common entity for DataReport) by DataReport
     * @param reportId
     * @return Report
     */
    public Report findReportByReportId (Long reportId){
        logger.info("Вызван метод findReportByReportId с параметром {}.", reportId);
        return reportRepository.findByIdReport(reportId);
    }

    /**
     * Method return List of all Report entity.
     * @return List<Report>
     */
    public List<Report> findAll (){
        logger.info("Вызван метод findAll.");
        return reportRepository.findAll();
    }
    /**
     * Method edit object Report, save it and return new version of Report.
     * @param report
     * @return Report
     */
    public Report editReport (Report report){
        logger.info("Вызван метод editReport .");
        return reportRepository.save(report);
    }


    public Report deleteReportByReportId(Long reportId){
        logger.info("Вызван метод deleteReportByReportId с параметром {}.", reportId);
        Optional<Report> report=reportRepository.findById(reportId);
        if (report.isPresent()) {
            List<DataReport> list=report.get().getDataReports();
            dataReportRepository.deleteAll(list);
            reportRepository.deleteById(reportId);
            return report.get();
        }
        throw new NoSuchElementException("нет такого отчёта");
    }

    public ArrayBlockingQueue<DataReport> getDataReportQueue() {
        logger.info("Вызван метод getDataReportQueue.");
        return dataReportQueue;
    }

    public Set<DataReport> getBadReport() {
        logger.info("Вызван метод getBadReport.");
        return badReport;
    }
}
