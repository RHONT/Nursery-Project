package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.PetRepository;
import com.nursery.nursery_api.service.PetService;
import lombok.SneakyThrows;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;

    @MockBean
    private PetService petService;

    @SpyBean
    private PetController petController;

    private static final LocalDate age = LocalDate.of(2022,8,15);

    private static final Pet pet1 = new Pet(null ,123L , "Bob", age ,null,false,null);


    @SneakyThrows
    @Test
    void addPet() {

        JSONObject petObject = new JSONObject();
        petObject.put("nickname",pet1.getNickname());

        when(petRepository.save(any(Pet.class))).thenReturn(pet1);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/nursery_app/admin_functions/pets/add_pet")
                .content(petObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.nickname"));



    }

    @Test
    void findInvalidPet() {
    }

    @Test
    void findHealthyPets() {
    }

    @Test
    void findPet() {
    }

    @Test
    void editPet() {
    }

    @Test
    void deletePetByName() {
    }
}
