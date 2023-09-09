package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Nursery;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NursaryRepository extends JpaRepository<Nursery,Long> {
    Nursery findByNameNursary(String nameNursery);
}
