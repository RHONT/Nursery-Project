package com.nursery.nursery_api.controllers;

import static java.lang.reflect.Array.get;
import static java.util.Optional.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.service.PersonService;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;


@WebMvcTest
public class PersonControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private PersonRepository personRepository;

    @MockBean
    private PersonService personService;

    @SpyBean
    private PersonController personController;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void addPersonToRepository () throws Exception{
        final String name = "Gregorio";
        final String phone = "123456789";
        final Long id = 888L;
        final Long chatId = 987654L;

        JSONObject personObject = new JSONObject();
        personObject.put("name", name);
        personObject.put("phone",phone);

        Person person = new Person();
        person.setIdPerson(id);
        person.setName(name);
        person.setPhone(phone);
        person.setIdChat(chatId);

        when(personRepository.save(any(Person.class))).thenReturn(person);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/add_person")
                .content(personObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_person").value(id))
                .andExpect(jsonPath("$.name").value(name))
                .andExpect(jsonPath("$.phone").value(phone))
                .andExpect(jsonPath("$.id_chat").value(chatId));

    }

    @Test
    public void testFindPersonByName() throws JSONException {
        final String name = "Gregorio";
        final String phone = "123456789";
        final Long id = 888L;
        final Long chatId = 987654L;

        Person person = new Person();
        person.setIdPerson(id);
        person.setName(name);
        person.setPhone(phone);
        person.setIdChat(chatId);

        personRepository.save(person);

        when(personService.findPersonByName(name)).thenReturn(person);

        ResponseEntity<Object> response = personController.findPerson(name, phone);


        verify(personService, times(1)).findPersonByName(name);
        verify(personService, times(1)).findPersonByPhone(phone);
        verify(personService, never()).findPersonByPhone(anyString());
        verify(personService, never()).findPersonByName(anyString());
        verify(personService, never()).findAllPersons();
        assertSame(response.getStatusCode(), HttpStatus.OK);
        assertSame(response.getBody(), person);
    }

    @Test
    public void testEditPerson() throws JSONException {

        final String name = "Gregorio";
        final String phone = "123456789";
        final Long id = 888L;
        final Long chatId = 987654L;

        JSONObject personObject = new JSONObject();
        personObject.put("name", name);
        personObject.put("phone",phone);

        Person person = new Person();
        person.setIdPerson(id);
        person.setName(name);
        person.setPhone(phone);
        person.setIdChat(chatId);

        when(personService.editPerson(person)).thenReturn(person);

        ResponseEntity<Person> response = personController.editPerson(person);

        verify(personService, times(1)).editPerson(person);
        assertSame(response.getStatusCode(), HttpStatus.OK);
        assertSame(response.getBody(), person);
    }

    @Test
    public void testDeletePersonByName() throws JSONException {
        final String name = "Gregorio";
        final String phone = "123456789";
        final Long id = 888L;
        final Long chatId = 987654L;

        JSONObject personObject = new JSONObject();
        personObject.put("name", name);
        personObject.put("phone",phone);

        Person person = new Person();
        person.setIdPerson(id);
        person.setName(name);
        person.setPhone(phone);
        person.setIdChat(chatId);

        when(personService.deletePersonByName(name)).thenReturn(null);

        ResponseEntity<Person> response = personController.deletePerson(name);

        verify(personService, times(1)).deletePersonByName(name);
        assertSame(response.getStatusCode(), HttpStatus.OK);
        assertNotNull(response.getBody());
    }
}
