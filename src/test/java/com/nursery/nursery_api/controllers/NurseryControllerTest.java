package com.nursery.nursery_api.controllers;

import static java.util.Optional.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.repositiry.NurseryRepository;
import com.nursery.nursery_api.service.NurseryService;
import lombok.SneakyThrows;
import org.assertj.core.error.OptionalShouldBePresent;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;

import javax.persistence.Column;
import java.util.Optional;

@WebMvcTest(NurseryController.class)
public class NurseryControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NurseryRepository nurseryRepository;

    @MockBean
    private NurseryService nurseryService;

    @SpyBean
    private NurseryController nurseryController;

    @SpyBean
    private PersonController personController;

    private static final Nursery bestNursery = new Nursery(999L,"any nursery","very good nursery","good","strong",
            "easy","like in prison","Nicolas CAGE","not alone","big","with cyber laboratory","dont feed after midnight",
            "dont put in water","not human","your credit card number and scv please",null);


/*    @Test
    public void addNurseryToRepositoryTest () throws Exception {


        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery",bestNursery.getNameNursery());

        when(nurseryRepository.save(any(Nursery.class))).thenReturn(bestNursery);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/nursery_app/admin_functions/nurseries/add_nursery")
                        .content(nurseryObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_nursery").value(bestNursery.getIdNursery()))
                .andExpect(jsonPath("$.name_nursery").value(bestNursery.getNameNursery()));

    }*/

    @Test
    public void deleteNurseryByName () throws JSONException {

        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery",bestNursery.getNameNursery());

        when(nurseryRepository.deleteNurseryByNameNursery(bestNursery.getNameNursery())).thenReturn(bestNursery);

    }


}
