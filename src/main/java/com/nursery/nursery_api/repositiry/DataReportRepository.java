package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface DataReportRepository extends JpaRepository<DataReport,Long> {
    Optional<DataReport> findByReportAndDateReport(Report report, LocalDate localDate);

    @Query(value = "select person.id_chat, person.name, person.phone, data_report.foto, data_report.message_person\n" +
            "from person\n" +
            "join report on person.id_person = report.id_person\n" +
            "join data_report on report.id_report = data_report.id_report\n" +
            "where data_report.check_message=false and report.day_report>0", nativeQuery = true)
    List<DataReport> findReportForCheck();

    @Query(value = "select report.*\n" +
            "from report\n" +
            "where report.day_report>0",nativeQuery = true)
    List<Report> findReportForInsertNewFields();




}
