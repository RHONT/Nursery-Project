package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Transactional
@Repository
public interface DataReportRepository extends JpaRepository<DataReport,Long> {
    Optional<DataReport> findByReportAndDateReport(Report report, LocalDate localDate);

    /**
     * Ищем запаси в dataReport где data_report.check_message=false, а значит нужно проверить волонтерам ее.
     * @return
     */
    @Query(value = "select data_report.*\n" +
            "from person\n" +
            "join report on person.id_person = report.id_person\n" +
            "join data_report on report.id_report = data_report.id_report\n" +
            "where data_report.check_message=false and report.day_report>0", nativeQuery = true)
    List<DataReport> findReportForCheck();



    @Query(value = "select data_report.*\n" +
            "from person\n" +
            "         join report on person.id_person = report.id_person\n" +
            "         join data_report on report.id_report = data_report.id_report\n" +
            "         join nursery on nursery.id_nursery = person.id_nursery\n" +
            "where person.id_chat=?\n" +
            "  and data_report.date_report=?\n" +
            "  and nursery.name_nursery=?",nativeQuery = true)
    Optional<DataReport> findDataReportByIdChatAndDateNow(Long idChatPerson,LocalDate localDate, String nameRepository);

    @Query(value = "SELECT data_report.* " +
            "FROM data_report " +
            "JOIN report ON data_report.id_report = report.id_report " +
            "WHERE data_report.date_report = :currentDate AND data_report.check_message = false", nativeQuery = true)
    List<DataReport> findDataReportsByDateReportAndCheckMessageFalse(@Param("currentDate") LocalDate currentDate);





}
