package com.nursery.nursery_api.service;

import com.nursery.nursery_api.dto.PersonDto;
import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.repositiry.NurseryRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class PersonService {
    Logger logger = LoggerFactory.getLogger(PersonService.class);

    private final PersonRepository personRepository;
    private final NurseryRepository nurseryRepository;

    public PersonService(PersonRepository personRepository, NurseryRepository nurseryRepository) {
        this.personRepository = personRepository;
        this.nurseryRepository = nurseryRepository;
    }


    public Person addPerson (PersonDto personDto){
        Nursery nursery=nurseryRepository.findByNameNursery(personDto.getNameNursery());
        Person person=Person.builder().name(personDto.getName()).phone(personDto.getPhone()).build();
        person.setNursery(nursery);
        logger.info("Вызван метод addPerson");
        return personRepository.save(person);
    }
    public Person findPersonByName (String personName){
        logger.info("Вызван метод findPersonByName с именем: {}", personName);
        return personRepository.findPersonByName(personName);
    }
    public Person findPersonByPhone (String phone){
        logger.info("Вызван метод findPersonByPhone с телефоном {}", phone);
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
        logger.info("Вызван метод deletePersonByName {}", personName);
        return personRepository.deletePersonByName(personName);
    }
}