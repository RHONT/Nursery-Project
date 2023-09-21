package com.nursery.nursery_api.shedul;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.VolunteerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;


@Component
public class ScheduledMethod {
    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;
    private final ReportService reportService;

    private final PersonRepository personRepository;
    private final TelegramBot telegramBot;
    private final ConnectService connectService;
    private final VolunteerService volunteerService;

    Logger logger = LoggerFactory.getLogger(VolunteerService.class);

    public ScheduledMethod(DataReportRepository dataReportRepository, ReportRepository reportRepository, PersonRepository personRepository, TaskExecutionProperties taskExecutionProperties, ReportService reportService, TelegramBot telegramBot, ConnectService connectService, VolunteerService volunteerService) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.reportService = reportService;
        this.telegramBot = telegramBot;
        this.connectService = connectService;
        this.volunteerService = volunteerService;
    }

    /**
     * Каждую ночь в 00:01,будет запускаться обновление базы
     */
    @Scheduled(cron = "0 1 0 * * *")
    private void createEmptyRowReport() {
        logger.info("Вызван метод createEmptyRowReport");
        List<Report> reportForInsertNewFields = dataReportRepository.findReportForInsertNewFields();
        for (var element : reportForInsertNewFields) {
            DataReport dataReport = new DataReport();
            dataReport.setReport(element);
            dataReport.setDateReport(LocalDate.now());
            dataReportRepository.save(dataReport);
        }
    }

    /**
     * В 23:55 метод очищает список непроверенных отчетов, отмечая их как проверенные, на случай перегруженности волонтеров.
     */
    @Scheduled(cron = "0 55 23 * * *")
    public void skippedReportsForDay (){
        logger.info("Вызван метод skippedReportsForDay");
        ArrayBlockingQueue checkArray = reportService.getDataReportQueue();
        List<DataReport> checkList = new ArrayList<>();
        checkArray.drainTo(checkList);
        if (!checkList.isEmpty()){
            for (var element : checkList){
                element.setCheckMessage(true);
                dataReportRepository.save(element);
            }
        }
    }

    /**
     * Каждую ночь в 23:59 достает из базы данных непроверенные отчеты за день и начисляет штраф тем,
     * кто их отослал.
     */
    @Scheduled(cron = "0 59 23 * * *")
    public void penaltyForBadReportForToday(){
        logger.info("Вызван метод penaltyForBadReportForToday");
        LocalDate currentDate = LocalDate.now();
        List<DataReport> reportCheck = dataReportRepository.findDataReportsByDateReportAndCheckMessageFalse(currentDate);
        for (var element : reportCheck){
            Report report = element.getReport();
            Long forfeit = report.getForteit();
            forfeit++;
            report.setForteit(forfeit);
            reportRepository.save(report);
        }
    }

    /**
     * В 10:00 ежедневно создает список тех, кто имеет более одного начисленного штрафа и посылает всем работающим волонтерам.
     */
    @Scheduled(cron = "0 0 10 * * *")
    public void shameListForVolunteers (){
        logger.info("Вызван метод shameListForVolunteers.");
        SendMessage message = new SendMessage();
        List<Report> checkReport = reportRepository.findAll();
        List<Person> shameList = new ArrayList<>();
        List<Long> freeVolunteersChats = volunteerService.freeVolunteersChatId();
        for (var element : checkReport){
            if(element.getForteit() >= 2){
                shameList.add(element.getPerson());
            }
        }
        String messageText = shameList.toString();
        message.setText(messageText);
        for (var chat : freeVolunteersChats){
            message.setChatId(chat);
            try {
                telegramBot.execute(message);
                logger.info("Сообщение волонтеру послано.");
            } catch (Exception e) {
                e.printStackTrace();
                logger.debug("Послать волонтеру сообщение е удалось.");
            }
        }
    }
}
