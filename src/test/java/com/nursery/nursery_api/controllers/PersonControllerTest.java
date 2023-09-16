package com.nursery.nursery_api.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.service.PersonService;
import org.json.JSONObject;
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


@AutoConfigureMockMvc
@SpringBootTest
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @SpyBean
    private PersonService personService;

    @InjectMocks
    private PersonController personController;

    private final Person personGreg = Person.builder()
            .idPerson(888L)
            .name("Gregorio")
            .idChat(987654L)
            .phone("123456789")
            .build();

    @Test
    public void addPerson() throws Exception {

    JSONObject personObject = new JSONObject();
    personObject.put("name", personGreg.getName());

    when(personRepository.save(any(Person.class))).thenReturn(personGreg);

    mockMvc.perform(MockMvcRequestBuilders
                    .post("/nursery_app/admin_functions/persons/add_person")
                    .content(personObject.toString())
                    .contentType(MediaType.APPLICATION_JSON)
                    .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value(personGreg.getName()));
    }

    @Test
    void findPersonByName() throws Exception {
        JSONObject personObject = new JSONObject();
        personObject.put("name", personGreg.getName());

        when(personRepository.findPersonByName(anyString())).thenReturn(personGreg);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/persons/find_person?name=Gregorio")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value(personGreg.getName()));
    }

    @Test
    void findPersonByPhone() throws Exception {

        JSONObject personObject = new JSONObject();
        personObject.put("name", personGreg.getName());
        personObject.put("phone", personGreg.getPhone());

        when(personRepository.findPersonByPhone(anyString())).thenReturn(personGreg);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/persons/find_person?phone=123456789")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.phone").value(personGreg.getPhone()));
    }

    @Test
    void editPerson() throws Exception {

        JSONObject personObject = new JSONObject();
        personObject.put("name", personGreg.getName());
        personObject.put("",personGreg.getIdPerson());

        when(personRepository.save(any(Person.class))).thenReturn(personGreg);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/nursery_app/admin_functions/persons/edit_person")
                        .content(personObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idPerson").value(personGreg.getIdPerson()))
                .andExpect(jsonPath("$.name").value(personGreg.getName()));
    }

    @Test
    void deletePersonByName() throws Exception {
        when(personRepository.findPersonByName(any(String.class))).thenReturn(personGreg);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/nursery_app/admin_functions/persons/delete_person_by_name?name=Gregorio")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

}
