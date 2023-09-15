package com.nursery.nursery_api.controllers;


import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.VolunteerService;
import net.minidev.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class VolunteersControllerTest {
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private VolunteerRepository volunteerRepository;
    @SpyBean
    private VolunteerService volunteerService;
    @InjectMocks
    private VolunteersController volunteersController;
    private static final Volunteer volunteer1 = new Volunteer(1L, 111L, "Иван", "+7 911", "@Master1", false);
    private static final Volunteer volunteer2 = new Volunteer(2L, 222L, "Марья", "+7 911", "@Master2", true);

//    @SneakyThrows
    @Test
    void addVolunteer() throws Exception {

        JSONObject volunteerObject = new JSONObject();
        volunteerObject.put("volunteer_name",volunteer1.getName());

        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer1);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/nursery_app/admin_functions/volunteers/add_volunteer")
                        .content(volunteerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(volunteer1.getName()));
    }

    @Test
    void findVolunteer() throws Exception {

        JSONObject volunteerObject = new JSONObject();
        volunteerObject.put("volunteer_name",volunteer1.getName());

        when(volunteerRepository.findByName(any(String.class))).thenReturn(volunteer1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/volunteers/find_volunteer?name=Иван")
                        .content(volunteerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(volunteer1.getName()));
    }

    @Test
    void editVolunteer() throws Exception {

        JSONObject volunteerObject = new JSONObject();
        volunteerObject.put("volunteer_name",volunteer1.getName());

        when(volunteerRepository.save(any(Volunteer.class))).thenReturn(volunteer1);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/nursery_app/admin_functions/volunteers/edit_volunteer")
                        .content(volunteerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(volunteer1.getName()));
    }

    @Test
    void deleteVolunteerByName() throws Exception {

        JSONObject volunteerObject = new JSONObject();
        volunteerObject.put("volunteer_name",volunteer2.getName());

        when(volunteerRepository.deleteVolunteerByName(volunteer2.getName())).thenReturn(volunteer2);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/nursery_app/admin_functions/volunteers/delete_volunteer_by_name?name=Марья")
                        .content(volunteerObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(volunteer2.getName()));



    }
}