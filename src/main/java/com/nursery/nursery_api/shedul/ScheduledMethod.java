package com.nursery.nursery_api.shedul;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class ScheduledMethod {
    private final DataReportRepository dataReportRepository;

    public ScheduledMethod(DataReportRepository dataReportRepository) {
        this.dataReportRepository = dataReportRepository;
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
}
