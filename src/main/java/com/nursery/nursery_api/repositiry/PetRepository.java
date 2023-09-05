package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Pet;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PetRepository extends JpaRepository<Pet,Long> {
}
