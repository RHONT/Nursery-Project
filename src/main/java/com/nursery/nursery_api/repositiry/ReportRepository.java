package com.nursery.nursery_api.repositiry;


import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportRepository extends JpaRepository<Report,Long> {

    @Query(value = "select * from Report where report.id_report = :idReport",nativeQuery = true)
    Report findByIdReport (@Param("idReport")Long idReport);

    @Query(value = "SELECT Report FROM Report WHERE report.id_person = :personId",nativeQuery = true)
    Report findReportByPersonId (@Param("personId") Long personId);

    Report deleteReportByIdReport (Long reportId);


}
