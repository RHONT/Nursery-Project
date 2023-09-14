package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.VolunteerService;
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

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest
class VolunteersControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private VolunteerRepository volunteerRepository;

    @MockBean
    private VolunteerService volunteerService;

    @SpyBean
    private VolunteersController volunteersController;

    private static final Volunteer volunteer1 = new Volunteer(1L, 111L, "Иван", "+7 911", "@Master1", false);
    private static final Volunteer volunteer2 = new Volunteer(2L, 222L, "Марья", "+7 911", "@Master2", true);

/*    @SneakyThrows
    @Test
    void addVolunteer() {

        JSONObject volunteerObject = new JSONObject();
        volunteerObject.put("volunteer_name",volunteer1.getName());

        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/nursery_app/admin_functions/pets/add_pet")
                        .content(volunteerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect((ResultMatcher) jsonPath("$.volunteer_name"));


    }*/

    @Test
    void findVolunteer() {
    }

    @Test
    void editVolunteer() {
    }

    @Test
    void deleteVolunteerByName() {
    }
}