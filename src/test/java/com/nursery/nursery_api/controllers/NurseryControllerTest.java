package com.nursery.nursery_api.controllers;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.repositiry.NurseryRepository;
import com.nursery.nursery_api.service.NurseryService;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

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
    public void createNursery() throws Exception {

        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery", bestNursery.getNameNursery());

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

    @Test
    void findNursery() throws Exception {

        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery", bestNursery.getNameNursery());

        when(nurseryRepository.findNurseryByNameNursery(anyString())).thenReturn(bestNursery);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/nurseries/find_nursery?nurseryName=Кошки")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNursery").value(1L));
    }

    @Test
    void editNursery() throws Exception {

        JSONObject nurseryObject = new JSONObject();
        nurseryObject.put("name_nursery", bestNursery.getNameNursery());

        when(nurseryRepository.save(any(Nursery.class))).thenReturn(bestNursery);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/nursery_app/admin_functions/nurseries/edit_nursery")
                        .content(nurseryObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.idNursery").value(bestNursery.getIdNursery()))
                .andExpect(jsonPath("$.nameNursery").value(bestNursery.getNameNursery()));
    }

    @Test
    void deleteNurseryByName() throws Exception {
        when(nurseryRepository.findNurseryByNameNursery(any(String.class))).thenReturn(bestNursery);
        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/nursery_app/admin_functions/nurseries/delete_nursery?nurseryName=Кошки")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }


}
