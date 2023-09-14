package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.repositiry.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class NurseryDBServiceTest {

    private NurseryDBService nurseryDBService;
    @Mock
    private DataReportRepository dataReportRepository;
    @Mock
    private NurseryRepository nurseryRepository;
    @Mock
    private PersonRepository personRepository;
    @Mock
    private PetRepository petRepository;
    @Mock
    private ReportRepository reportRepository;
    @Mock
    private VisitorsRepository visitorsRepository;
    @Mock
    private VolunteerRepository volunteerRepository;

    @BeforeEach
    public void setUp(){
        nurseryDBService = new NurseryDBService(dataReportRepository, nurseryRepository, personRepository,
                petRepository, reportRepository, visitorsRepository, volunteerRepository);

        Visitors visitor = new Visitors();
        visitor.setNameNursery("test");
        visitor.setChatId(1L);
        List<Visitors> listFromDBVisitors = new ArrayList<>();
        listFromDBVisitors.add(visitor);

        Mockito.when(visitorsRepository.findAll()).thenReturn(listFromDBVisitors);

    }
//    @Test
//    void containTest1(){
//
//
//        boolean expected = true;
//        boolean actual = nurseryDBService.contain(1L);
//
//        assertEquals(expected, actual);
//    }
//
    @Test
    void containTest2(){

        boolean expected = false;
        boolean actual = nurseryDBService.contain(2L);

        assertEquals(expected, actual);
    }
}
