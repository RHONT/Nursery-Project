package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.service.NurseryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/nursery_app/admin_functions/nurseries")
public class NurseryController {
    private final NurseryService nurseryService;
    public NurseryController(NurseryService nurseryService) {
        this.nurseryService = nurseryService;
    }

    //добавить приют
    @Operation(summary = "Добавление приюта в базу данных",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленный приют.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Nursery.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Создаваемый объект класс Nursery.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class)
                    )
            ),
            tags = "Nursery"
    )
    @PostMapping(path = "/add_nursery")
    public Nursery createNursery(@RequestBody Nursery nursery){
        return nurseryService.createNursery(nursery);
    }

    //Найти приют
    @Operation(summary = "Поиск приюта по имени или выведение всех приютов списком",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденный приют/приюты.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Nursery.class))
                            )
                    )
            },
            tags = "Nursery"
    )
    @GetMapping(path = "/find_nursery")
    public ResponseEntity<Object> findNursery (@RequestParam String nurseryName){
        if ( nurseryName!=null && !nurseryName.isBlank()){
            return ResponseEntity.ok(nurseryService.findNurseryByName(nurseryName));
        }
        return ResponseEntity.ok(nurseryService.getAllNursery());
    }
    //редактировать приют
    @Operation(summary = "Редактирование приюта.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Измененный приют.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Nursery.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Изменяемый объект класс Nursery.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Nursery.class)
                    )
            ),
            tags = "Nursery")
    @PutMapping(path ="/edit_nursery")
    public ResponseEntity<Nursery> editNursery (@RequestBody  Nursery nursery){
        Nursery editedNursery = nurseryService.editNursery(nursery);
        if(editedNursery == null){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        return ResponseEntity.ok(editedNursery);
    }
    //удалить приют
    @Operation(summary = "Удаление приюта по его имени",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Удаленный приют.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Nursery.class))
                            )
                    )
            },
            tags = "Nursery"
    )
    @DeleteMapping(path = "/delete_nursery")
    public ResponseEntity<Nursery> deleteNurseryByName(@RequestParam String nurseryName){
        return ResponseEntity.ok(nurseryService.deleteNurseryByName(nurseryName));
    }

}