package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.service.VolunteerService;
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
    @PostMapping(path = "/add_volunteer")
    public ResponseEntity<Volunteer> addVolunteer(@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.addVolunteer(volunteer));
    }
    //найти волонтера
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
    @PutMapping(path = "/edit_volunteer")
    public ResponseEntity<Volunteer> editVolunteer (@RequestBody Volunteer volunteer){
        return ResponseEntity.ok(volunteerService.editVolunteer(volunteer));
    }
    //удалить волонтера

    @DeleteMapping(path = "/delete_volunteer_by_name")
    public ResponseEntity<Volunteer> deleteVolunteerByName (@RequestParam String name){
        return ResponseEntity.ok(volunteerService.deleteVolunteerByName(name));
    }
}
