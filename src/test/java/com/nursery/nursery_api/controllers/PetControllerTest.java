package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.PetRepository;
import com.nursery.nursery_api.service.PetService;
import lombok.SneakyThrows;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.LocalDate;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
public class PetControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PetRepository petRepository;

    @SpyBean
    private PetService petService;

    @InjectMocks
    private PetController petController;

    private static final LocalDate age = LocalDate.of(2022,8,15);

    private final Pet tom = Pet.builder()
            .nickname("Tom")
            .idPet(123L)
            .nursery(null)
            .age(age)
            .foto(null)
            .invalid(false)
            .person(null)
            .build();



    @Test
    void addPet() throws Exception {

        JSONObject petObject = new JSONObject();
        petObject.put("nickname",tom.getNickname());

        when(petRepository.save(any(Pet.class))).thenReturn(tom);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/nursery_app/admin_functions/pets/add_pet")
                .content(petObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(tom.getNickname()));
    }

    @Test
    void findPetByName() throws Exception{
        JSONObject petObject = new JSONObject();
        petObject.put("nickname", tom.getNickname());

        when(petRepository.findPetByNickname(anyString())).thenReturn(tom);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/pets//find_pet?petName=Tom")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nickname").value(tom.getNickname()));
    }



    @Test
    void editPet() throws Exception {

    org.json.JSONObject petObject = new JSONObject();
    petObject.put("nickname", tom.getNickname());

    when(petRepository.save(any(Pet.class))).thenReturn(tom);

    mockMvc.perform(MockMvcRequestBuilders
                    .put("/nursery_app/admin_functions/pets/edit_pet")
                    .content(petObject.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.nickname").value("Tom"));
    }



    @Test
    void deletePetByName() throws Exception {
            when(petRepository.deletePetByNickname(any(String.class))).thenReturn(tom);
        mockMvc.perform(MockMvcRequestBuilders
        .delete("/nursery_app/admin_functions/pets/delete_pet?petName=Bob")
        .accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk());
        }
}
