package com.nursery.nursery_api.shedul;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


@Component
public class ScheduledMethod {
    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;

    private final PersonRepository personRepository;
    private final TelegramBot telegramBot;

    public ScheduledMethod(DataReportRepository dataReportRepository, ReportRepository reportRepository, PersonRepository personRepository, TaskExecutionProperties taskExecutionProperties, TelegramBot telegramBot) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.telegramBot = telegramBot;
    }

    /**
     * Каждую ночь в 00:01,будет запускаться обновление базы
     */
    @Scheduled(cron = "0 1 0 * * *")
    private void createEmptyRowReport() {
        List<Report> reportForInsertNewFields = dataReportRepository.findReportForInsertNewFields();
        for (var element : reportForInsertNewFields) {
            DataReport dataReport = new DataReport();
            dataReport.setReport(element);
            dataReport.setDateReport(LocalDate.now());
            dataReportRepository.save(dataReport);
        }
    }

    /**
     * Каждую ночь в 23:59 достает из базы данных непроверенные отчеты за день и начисляет штраф тем,
     * кто их отослал.
     */
    @Scheduled(cron = "59 23 * * *")
    public void penaltyForBadReportForToday(){
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
    @Scheduled(cron = "0 10 * * *")
    public void shameListForVolunteers (){
        SendMessage message = new SendMessage();
        List<Report> checkReport = reportRepository.findAll();
        List<Person> shameList = new ArrayList<>();
        for (var element : checkReport){
            if(element.getForteit() >= 2){
                shameList.add(element.getPerson());
            }
        }
        message.setChatId("не знаю точно, что там писать для подставления чат ид волонтеров - полагаю это уже есть где-то в коде");
        String messageText = shameList.toString();
        message.setText(messageText);
        try {
            telegramBot.execute(message);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
