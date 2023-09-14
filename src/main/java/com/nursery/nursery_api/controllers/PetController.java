package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.service.PetService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import liquibase.pro.packaged.R;
import org.springframework.http.MediaType;
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

    @Operation(summary = "Добавление животного в базу.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленное животное.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создаваемый объект класс Pet.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),
            tags = "Pet"
    )
    @PostMapping(path = "/add_pet")
    public ResponseEntity<Pet> addPet(@RequestBody Pet pet){
        return ResponseEntity.ok(petService.addPet(pet));
    }

    //найти животное

    @Operation(summary = "Поиск всех животных с инвалидностью.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список найденных животных.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            tags = "Pet"
    )
    @GetMapping(path = "/find_invalid_pets")
    public ResponseEntity<List<Pet>> findInvalidPet (){
        return ResponseEntity.ok(petService.findPetsByInvalid());
    }

    @Operation(summary = "Поиск всех здоровых животных по базе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Список найденных здоровых животных.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            tags = "Pet"
    )
    @GetMapping(path = "/find_healthy_pets")
    public ResponseEntity<List<Pet>> findHealthyPets (){
        return ResponseEntity.ok(petService.findHealthyPets());
    }

    @Operation(summary = "Поиск животного в базе по имени животного или приюта к которому оно приписано.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденное животное/ые.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            tags = "Pet"
    )
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

    @Operation(summary = "Редактирование животного в базе.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Отредактированное животное.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Редактируемый объект класс Pet.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Pet.class)
                    )
            ),
            tags = "Pet"
    )
    @PutMapping(path = "/edit_pet")
    public ResponseEntity<Pet> editPet (@RequestBody Pet pet){
        return ResponseEntity.ok(petService.editPet(pet));
    }

    //удалить животное

    @Operation(summary = "Удаление животного из базы по имени.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленное животное.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Pet.class))
                            )
                    )
            },
            tags = "Pet"
    )
    @DeleteMapping(path = "/delete_pet")
    public ResponseEntity<Pet> deletePetByName (@RequestParam String petName){
        return ResponseEntity.ok(petService.deletePetByName(petName));
    }
}
