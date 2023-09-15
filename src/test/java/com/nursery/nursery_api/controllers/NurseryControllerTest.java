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
import org.mockito.InjectMocks;
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

    @SpyBean
    private NurseryService nurseryService;

    @InjectMocks
    private NurseryController nurseryController;

    private final Nursery bestNursery = Nursery.builder().
            idNursery(1L).
            nameNursery("Кошки").
            listDocument("Паспорт").
            about("О приюте").
            infrastructure("Инфраструктура").
            howGetPet("Забрать").
            accidentPrevention("Правила нахождения").
            reasonsRefusal("Причина отказа").
            transportRule("Правила транспортировки").
            datingRule("Перва встреча").
            houseRecomendAdult("рекомендации").
            houseRecomendBaby("рекомендации").
            houseRecommendInvalid("рекомендации").
            cynologistAdvice("кинолог").
            cynologistAdviceUp("кинолог плюс")
            .build();


    @Test
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
                .andExpect(jsonPath("$.idNursery").value(bestNursery.getIdNursery()))
                .andExpect(jsonPath("$.nameNursery").value(bestNursery.getNameNursery()));

    }

//    @Test
//    public void deleteNurseryByName () throws JSONException {
//
//        JSONObject nurseryObject = new JSONObject();
//        nurseryObject.put("name_nursery",bestNursery.getNameNursery());
//
//        when(nurseryRepository.deleteNurseryByNameNursery(bestNursery.getNameNursery())).thenReturn(bestNursery);
//
//    }


}
