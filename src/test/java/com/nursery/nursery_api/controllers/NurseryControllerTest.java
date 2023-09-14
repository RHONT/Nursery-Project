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
import org.assertj.core.error.OptionalShouldBePresent;
import org.json.JSONException;
import org.json.JSONObject;
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

    @Test
    public void addNurseryToRepositoryTest () throws Exception {
        Long idNursery = 999L;
        String nameNursery ="any nursery";
        String about = "very good nursery";
        String infrastructure = "good";
        String accidentPrevention = "strong";
        String howGetPet = "easy";
        String datingRule = "like in prison";
        String transportRule = "Nicolas CAGE";
        String houseRecomendBaby = "not alone";
        String houseRecomendAdult = "big";
        String houseRecommendInvalid = "with cyber laboratory";
        String cynologistAdvice = "dont feed after midnight";
        String cynologistAdviceUp = "dont put in water";
        String reasonsRefusal = "not human";
        String listDocument = "your credit card number and scv please";
        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery",nameNursery);
        Nursery bestNursery = new Nursery();
        bestNursery.setIdNursery(idNursery);
        bestNursery.setAbout(about);
        bestNursery.setInfrastructure(infrastructure);
        bestNursery.setAccidentPrevention(accidentPrevention);
        bestNursery.setHowGetPet(howGetPet);
        bestNursery.setDatingRule(datingRule);
        bestNursery.setTransportRule(transportRule);
        bestNursery.setHouseRecomendBaby(houseRecomendBaby);
        bestNursery.setHouseRecomendAdult(houseRecomendAdult);
        bestNursery.setHouseRecommendInvalid(houseRecommendInvalid);
        bestNursery.setCynologistAdvice(cynologistAdvice);
        bestNursery.setCynologistAdviceUp(cynologistAdviceUp);
        bestNursery.setReasonsRefusal(reasonsRefusal);
        bestNursery.setListDocument(listDocument);

        when(nurseryRepository.save(any(Nursery.class))).thenReturn(bestNursery);

        mockMvc.perform(MockMvcRequestBuilders
                .post("/add_nursery")
                .content(nurseryObject.toString())
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id_nursery").value(idNursery))
                .andExpect(jsonPath("$.name_nursery").value(nameNursery));

    }
}
