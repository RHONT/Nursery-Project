package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PersonRepository extends JpaRepository<Person,Long> {
}
