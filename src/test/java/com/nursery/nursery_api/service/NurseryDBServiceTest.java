package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.repositiry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)

public class NurseryDBServiceTest {

    @Mock
    private NurseryRepository nurseryRepository;
    @Mock
    private VisitorsRepository visitorsRepository;
    @InjectMocks
    private NurseryDBService nurseryDBService;

    List<Visitors> listFromDBVisitors = new ArrayList<>();


    @BeforeEach
    public void setUp(){
        Visitors visitor = new Visitors();
        visitor.setNameNursery("test");
        visitor.setChatId(1L);
        listFromDBVisitors.add(visitor);
    }
    @Test
    void containTest1(){
//        when(visitorsRepository.findAll()).thenReturn(listFromDBVisitors);

        nurseryDBService.contain(1L);
        boolean expected = true;
        boolean actual = nurseryDBService.contain(1L);

        assertEquals(expected, actual);
    }

    @Test
    void containTest2(){

        boolean expected = false;
        boolean actual = nurseryDBService.contain(2L);

        assertEquals(expected, actual);
    }
}
