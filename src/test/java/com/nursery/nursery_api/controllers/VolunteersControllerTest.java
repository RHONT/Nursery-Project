package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.VolunteerService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

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
    private static final Volunteer volunteer2 = new Volunteer(2L, 222L, "Марья", "+7 911", "@Master2", false);

    @Test
    void addVolunteer() {


    }

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