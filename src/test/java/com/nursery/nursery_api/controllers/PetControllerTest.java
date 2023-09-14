package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.PetRepository;
import com.nursery.nursery_api.service.PetService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

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


    @Test
    void addPet() {

        when(petRepository.save(any(Pet.class))).thenReturn(pet1);



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
