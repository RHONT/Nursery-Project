package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.repositiry.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class NurseryDBServiceTest {

    @Mock
    private NurseryRepository nurseryRepository;

    @InjectMocks
    private NurseryDBService nurseryDBService;

    private final Nursery nurseryTest = Nursery.builder().
            idNursery(1L).
            nameNursery("Кошки").
            listDocument("Паспорт").about("О приюте").
            infrastructure("Инфраструктура").
            howGetPet("Забрать").
            accidentPrevention("Правила нахождения").
            reasonsRefusal("Причина отказа").
            transportRule("Правила транспортировки")
            .build();

    @Test
    void containTestReturnTrue() {
        nurseryDBService.contain(1L);
        boolean expected = true;
        boolean actual = nurseryDBService.contain(1L);
        assertEquals(expected, actual);
    }

    @Test
    void containTestReturnFalse() {
        boolean expected = false;
        boolean actual = nurseryDBService.contain(2L);
        assertEquals(expected, actual);
    }

    @Test
    void getMeAboutNursery() {
        nurseryDBService.contain(111L);
        when(nurseryRepository.findByNameNursery("Кошки")).thenReturn(nurseryTest);
        nurseryDBService.setNurseryIntoVisitors(111L, "Кошки");

        String actual = nurseryDBService.getMeAboutNursery(111L);
        String expected = "О приюте";
        assertEquals(expected, actual);
    }

    @Test
    void getInfrastructure() {
        nurseryDBService.contain(111L);
        when(nurseryRepository.findByNameNursery("Кошки")).thenReturn(nurseryTest);
        nurseryDBService.setNurseryIntoVisitors(111L, "Кошки");

        String actual = nurseryDBService.getInfrastructure(111L);
        String expected = "Инфраструктура";
        assertEquals(expected, actual);
    }

    @Test
    void getDocument() {
        nurseryDBService.contain(111L);
        when(nurseryRepository.findByNameNursery("Кошки")).thenReturn(nurseryTest);
        nurseryDBService.setNurseryIntoVisitors(111L, "Кошки");

        String actual = nurseryDBService.getDocument(111L);
        String expected = "Паспорт";
        assertEquals(expected, actual);
    }

}
