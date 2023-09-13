package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.mock.mockito.SpyBean;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConnectServiceTest {

    @Mock
    VolunteerRepository volunteerRepository;


    @InjectMocks
    ConnectService connectService;

    @Test
    void addQueueMessage() {
    }

    @Test
    void manageVolunteerAndPerson() {
    }

    @Test
    void addToDialogsUser() {
    }

    @Test
    void disconnect() {
    }

    @Test
    void disappearanceVolunteer() {
    }

    @Test
    void addNewVolunteer() {
        Volunteer volunteer = new Volunteer(1l, 666l, "Иван", "+7 911", "@Master", false);
        when(volunteerRepository.save(volunteer)).thenReturn(volunteer);
        assertEquals(volunteer,connectService.addNewVolunteer(volunteer));


    }

    @Test
    void hasLeftVolunteer() {
    }

    @Test
    void goOnShiftVolunteer() {
    }

    @Test
    void getVolunteerChatIdByPersonChatId() {
    }
}