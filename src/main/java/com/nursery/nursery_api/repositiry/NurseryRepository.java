package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Nursery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface NurseryRepository extends JpaRepository<Nursery,Long> {
    Nursery findByNameNursery (String nurseryName);
    Nursery deleteNurseryByNameNursery (String nurseryName);
    @Query(value = "SELECT id_nursery FROM Nursary WHERE name_nursery =: nurseryName", nativeQuery = true)
    Long nurseryIdByName (@Param("nurseryName")String nurseryName);
}
