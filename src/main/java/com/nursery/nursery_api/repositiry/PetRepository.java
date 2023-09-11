package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PetRepository extends JpaRepository<Pet,Long> {
    Pet findPetByNickname (String petName);
    Pet deletePetByNickname (String petName);
    @Query(value = "SELECT * FROM Pet WHERE pet.id_nursary = :nurseryId", nativeQuery = true)
    List<Pet> findPetsByNurseryId (@Param("nurseryId") Long nurseryId);

    List<Pet> findPetsByInvalidTrue ();
    List<Pet> findPetsByInvalidFalse ();
}
