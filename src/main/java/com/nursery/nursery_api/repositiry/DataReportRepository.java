package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.DataReport;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DataReportRepository extends JpaRepository<DataReport,Long> {
}
