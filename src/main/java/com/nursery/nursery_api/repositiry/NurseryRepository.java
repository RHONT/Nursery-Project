package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Nursery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface NurseryRepository extends JpaRepository<Nursery,Long> {
    Nursery findByNameNursary(String nameNursery);
    Nursery findNurseryByNameNursery (String nurseryName);
    Nursery deleteNurseryByNameNursery (String nurseryName);
    @Query(value = "SELECT id_nursary FROM Nursary WHERE name_nursary =: nurseryName", nativeQuery = true)
    Long nurseryIdByName (@Param("nurseryName")String nurseryName);
}
