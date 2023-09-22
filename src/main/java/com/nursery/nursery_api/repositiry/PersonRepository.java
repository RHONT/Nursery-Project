package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Person;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PersonRepository extends JpaRepository<Person, Long> {


    Person findPersonByName(String personName);
    //todo написать запрос в базу
    Optional<Person> findPersonByNamePersonAndByIdNursery(String namePerson, Long idNursery);

    Person findPersonByPhone(String phone);

    Person deletePersonByName(String personName);

    @Query(value = "select person.id_chat\n" +
            "from person\n" +
            "join report r on person.id_person = r.id_person\n" +
            "join data_report dr on r.id_report = dr.id_report\n" +
            "where dr.id_data_report=?", nativeQuery = true)
    Optional<Long> findChatIdPersonByIdDataReport(Long idDataReport);

}
