package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.repositiry.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PersonService {
    PersonRepository personRepository;

    public PersonService(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }
    Logger logger = LoggerFactory.getLogger(NurseryService.class);

    public Person addPerson (Person person){
        logger.info("Вызван метод addPerson");
        return personRepository.save(person);
    }
    public Person findPersonByName (String personName){
        logger.info("Вызван метод findPersonByName");
        return personRepository.findPersonByName(personName);
    }
    public Person findPersonByPhone (String phone){
        logger.info("Вызван метод findPersonByPhone");
        return personRepository.findPersonByPhone(phone);
    }
    public List<Person> findAllPersons (){
        logger.info("Вызван метод findAllPersons");
        return personRepository.findAll();
    }

    public Person editPerson (Person person){
        logger.info("Вызван метод editPerson");
        return personRepository.save(person);
    }

    public Person deletePersonByName (String personName){
        logger.info("Вызван метод deletePersonByName");
        return personRepository.deletePersonByName(personName);
    }
}