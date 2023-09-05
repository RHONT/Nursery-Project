package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.model.Report;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReportRepository extends JpaRepository<Report,Long> {
}
