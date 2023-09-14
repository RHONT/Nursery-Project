package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Visitors;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitorsRepository extends JpaRepository<Visitors,Long> {
}
