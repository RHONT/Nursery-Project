package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.service.PersonService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/nursery_app/admin_functions/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @PostMapping(path="/add_person")
    public ResponseEntity<Person> addPerson (@RequestBody Person person){
        return ResponseEntity.ok(personService.addPerson(person));
    }

    @GetMapping(path = "/find_person")
    public ResponseEntity<Object> findPerson (@RequestParam(required = false) String name,
                                              @RequestParam(required = false) String phone ){
        if(name != null && !name.isBlank()){
            return ResponseEntity.ok(personService.findPersonByName(name));
        }
        if(phone != null && !phone.isBlank()){
            return ResponseEntity.ok(personService.findPersonByPhone(phone));
        }
        return ResponseEntity.ok(personService.findAllPersons());
    }
    @PutMapping(path = "/edit_person")
    public ResponseEntity<Person> editPerson (@RequestBody Person person){
        return ResponseEntity.ok(personService.editPerson(person));
    }
    @DeleteMapping(path = "/delete_person_by_name")
    public ResponseEntity<Person> deletePerson (@RequestParam String name){
        return ResponseEntity.ok(personService.deletePersonByName(name));
    }
}