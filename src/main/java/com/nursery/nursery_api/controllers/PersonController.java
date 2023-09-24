package com.nursery.nursery_api.controllers;


import com.nursery.nursery_api.dto.PersonDto;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.service.PersonService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping(path = "/nursery_app/admin_functions/persons")
public class PersonController {
    private final PersonService personService;

    public PersonController(PersonService personService) {
        this.personService = personService;
    }

    @Operation(summary = "Добавление посетителя приюта в базу.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленный посетитель.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Person.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создаваемый объект класс Person.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = PersonDto.class)
                    )
            ),
            tags = "Person"
    )
    @PostMapping(path="/add_person")
    public ResponseEntity<Person> addPerson (@RequestBody PersonDto personDto){
        return ResponseEntity.ok(personService.addPerson(personDto));
    }

    @Operation(summary = "Поиск посетителя в базе по имени или телефону.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный посетитель.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Person.class))
                            )
                    )
            },
            tags = "Person"
    )
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

    @Operation(summary = "Редактирование посетителя приюта в базе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отредактированный посетитель.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Person.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый объект класс Person.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Person.class)
                    )
            ),
            tags = "Person"
    )
    @PutMapping(path = "/edit_person")
    public ResponseEntity<Person> editPerson (@RequestBody Person person){
        return ResponseEntity.ok(personService.editPerson(person));
    }

    @Operation(summary = "Удаление посетителя приюта из базы по имени.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный посетитель.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Person.class))
                            )
                    )
            },
            tags = "Person"
    )
    @DeleteMapping(path = "/delete_person_by_name")
    public ResponseEntity<Person> deletePerson (@RequestParam String name){
        return ResponseEntity.ok(personService.deletePersonByName(name));
    }
}