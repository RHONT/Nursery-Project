package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.service.PetService;
import liquibase.pro.packaged.R;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/nursery_app/admin_functions/pets")
public class PetController {
    private PetService petService;
    public PetController(PetService petService) {
        this.petService = petService;
    }

    //добавить животное
    @PostMapping(path = "/add_pet")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet){
        return ResponseEntity.ok(petService.addPet(pet));
    }
    //найти животное

    @GetMapping(path = "/find_invalid_pets")
    public ResponseEntity<List<Pet>> findInvalidPet (){
        return ResponseEntity.ok(petService.findPetsByInvalid());
    }
    @GetMapping(path = "/find_healthy_pets")
    public ResponseEntity<List<Pet>> findHealthyPets (){
        return ResponseEntity.ok(petService.findHealthyPets());
    }
    @GetMapping(path = "/find_pet")
    public ResponseEntity<Object> findPet (@RequestParam(required = false) String petName,
                                           @RequestParam(required = false) String nurseryName){
        if (petName != null && !petName.isBlank()){
            return ResponseEntity.ok(petService.findPetByName(petName));
        }
        if (nurseryName != null && !nurseryName.isBlank()){
            return ResponseEntity.ok(petService.findPetsByNurseryName(nurseryName));
        }
        return ResponseEntity.ok(petService.findAll());
    }
    //редактировать животное
    @PutMapping(path = "/edit_pet")
    public ResponseEntity<Pet> editPet (@RequestBody Pet pet){
        return ResponseEntity.ok(petService.editPet(pet));
    }

    //удалить животное
    @DeleteMapping(path = "/delete_pet")
    public ResponseEntity<Pet> deletePetByName (@RequestParam String petName){
        return ResponseEntity.ok(petService.deletePetByName(petName));
    }
}
