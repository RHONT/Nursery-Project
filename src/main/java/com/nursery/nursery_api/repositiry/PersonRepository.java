package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Person,Long> {
    Person findPersonByName (String personName);
    Person findPersonByPhone (String phone);
    Person deletePersonByName (String personName);
}
