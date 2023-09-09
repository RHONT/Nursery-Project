package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Nursary;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NursaryRepository extends JpaRepository<Nursary,Long> {
    Nursary findByNameNursary(String nameNursery);
}
