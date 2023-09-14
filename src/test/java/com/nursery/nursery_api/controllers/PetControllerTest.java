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

    private static final Pet pet1 = new Pet(null ,123L , "Bob", 2,null,false,null);


    @Test
    void addPet() {
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
