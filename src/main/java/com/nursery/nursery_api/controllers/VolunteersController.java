package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.service.VolunteerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nursery_app/admin_functions/volunteers")
public class VolunteersController {
    VolunteerService volunteerService;
    public VolunteersController(VolunteerService volunteerService) {
        this.volunteerService = volunteerService;
    }

    //добавить волонтера

    @Operation(summary = "Добавление волонтера в базу.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленный волонтер.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создаваемый объект класса Volunteer.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Volunteer.class)
                    )
            ),
            tags = "Volunteer"
    )
    @PostMapping(path = "/add_volunteer")
    public ResponseEntity<Volunteer> addVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.addVolunteer(volunteer));
    }
    //найти волонтера

    @Operation(summary = "Поиск волонтера в базе по имени или телефону.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный волонтер.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Volunteer"
    )
    @GetMapping(path = "/find_volunteer")
    public ResponseEntity<Object> findVolunteer (@RequestParam(required = false) String name,
                                                 @RequestParam(required = false) String phone){
        if(name != null && !name.isBlank()) {
            return ResponseEntity.ok(volunteerService.findVolunteerByName(name));
        }
        if(phone != null && !phone.isBlank()){
            return ResponseEntity.ok(volunteerService.findByPhone(phone));
        }
        return ResponseEntity.ok(volunteerService.findAllVolunteers());
    }

    //редактировать волонтера

    @Operation(summary = "Редактирование волонтера в базе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отредактированный волонтер.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый объект класса Volunteer.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Volunteer.class)
                    )
            ),
            tags = "Volunteer"
    )
    @PutMapping(path = "/edit_volunteer")
    public ResponseEntity<Volunteer> editVolunteer (@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.editVolunteer(volunteer));
    }
    //удалить волонтера


    @Operation(summary = "Удаление волонтера из базу.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный волонтер.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Volunteer.class))
                            )
                    )
            },
            tags = "Volunteer"
    )
    @DeleteMapping(path = "/delete_volunteer_by_name")
    public ResponseEntity<Volunteer> deleteVolunteerByName (@RequestParam String name){
        return ResponseEntity.ok(volunteerService.deleteVolunteerByName(name));
    }
}
