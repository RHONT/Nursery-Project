package com.nursery.nursery_api.service;

import com.nursery.nursery_api.SomeClasses.PostMessagePerson;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import static org.mockito.Mockito.*;

import org.springframework.boot.test.mock.mockito.SpyBean;

import javax.validation.constraints.AssertTrue;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ConnectServiceTest {

    @Mock
    VolunteerRepository volunteerRepository;
    @Mock
    TelegramBot telegramBot;


    @InjectMocks
    ConnectService connectService;

    private static final Volunteer volunteer1 = new Volunteer(1L, 111L, "Иван", "+7 911", "@Master1", false);
    private static final Volunteer volunteer2 = new Volunteer(2L, 222L, "Марья", "+7 911", "@Master2", false);
    private static final Volunteer volunteer3 = new Volunteer(3L, 333L, "Гена", "+7 911", "@Master3", true);
    private static final Volunteer volunteer4 = new Volunteer(4L, 444L, "Лера", "+7 911", "@Master4", true);

    @BeforeEach
    void clearList(){
        connectService.getDialogs().clear();
        connectService.getVolunteersList().clear();
        connectService.getQueueMessage().clear();
        volunteer1.setBusy(false);
        volunteer2.setBusy(false);
        volunteer3.setBusy(true);
        volunteer4.setBusy(true);
    }

    @Test
    void addQueueMessage() {
        PostMessagePerson p1=new PostMessagePerson(1L,"1test");
        PostMessagePerson p2=new PostMessagePerson(2L,"2test");
        connectService.addQueueMessage(p1);
        connectService.addQueueMessage(p2);
        assertEquals(2,connectService.getQueueMessage().size());
    }

    @Test
    void manageVolunteerAndPerson() {
        PostMessagePerson p1=new PostMessagePerson(1L,"1test");
        PostMessagePerson p2=new PostMessagePerson(2L,"2test");

        connectService.addQueueMessage(p1);
        connectService.addQueueMessage(p2);

        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer3);

        connectService.manageVolunteerAndPerson();

        assertEquals(1,connectService.getQueueMessage().size());
        assertFalse(connectService.freeVolunteers(connectService.getVolunteersList()));
    }

    @Test
    void addToDialogsUser() {
        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer3);
        connectService.addToDialogsUser(1L);

        assertTrue(volunteer1.isBusy());
        assertTrue(connectService.getDialogs().contains(volunteer1.getVolunteerId()));
        assertTrue(connectService.getDialogs().contains(1L));
        assertFalse(connectService.getDialogs().contains(volunteer3.getVolunteerId()));

        volunteer1.setBusy(false);
    }

    @Test
    void disconnect() {
        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer3);
        connectService.addToDialogsUser(1L);

        connectService.disconnect(volunteer1.getVolunteerChatId());

        assertFalse(connectService.getDialogs().contains(1L));
        assertFalse(connectService.getDialogs().contains(volunteer1.getVolunteerChatId()));
        assertFalse(connectService.getDialogs().contains(volunteer3.getVolunteerChatId()));
        assertEquals(0, connectService.getDialogs().size());
        assertEquals(2, connectService.getVolunteersList().size());

        assertFalse(volunteer1.isBusy());
    }

    @Test
    void addNewVolunteer() {
        when(volunteerRepository.save(volunteer1)).thenReturn(volunteer1);
        assertEquals(volunteer1,connectService.addNewVolunteer(volunteer1));
        connectService.addNewVolunteer(volunteer2);
        assertEquals(2,connectService.getVolunteersList().size());
    }

    @Test
    void hasLeftVolunteer() {
        when(volunteerRepository.findByVolunteerChatId(anyLong())).thenReturn(Optional.of(volunteer1));
        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer2);
        connectService.hasLeftVolunteer(111L);
        assertEquals(1,connectService.getVolunteersList().size());
    }

    @Test
    void goOnShiftVolunteer() {
        connectService.addNewVolunteer(volunteer3);
        connectService.goOnShiftVolunteer(volunteer3.getVolunteerChatId());
        assertFalse(volunteer3.isBusy());
        volunteer3.setBusy(true);
    }

    @Test
    void getVolunteerChatIdByPersonChatId() {
        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer2);

        connectService.addToDialogsUser(1L);
        connectService.addToDialogsUser(2L);

        Long v1 = connectService.getVolunteerChatIdByPersonChatId(1L);
        Long v2 = connectService.getVolunteerChatIdByPersonChatId(2L);

        Set<Long> temp=new HashSet<>();

        temp.add(v1);
        temp.add(v2);

        assertTrue(temp.contains(111L));
        assertTrue(temp.contains(222L));
    }

    @Test
    void freeVolunteers() {
        connectService.addNewVolunteer(volunteer1);
        connectService.addNewVolunteer(volunteer3);
        connectService.addNewVolunteer(volunteer4);
        assertTrue(connectService.freeVolunteers(connectService.getVolunteersList()));
        connectService.disappearanceVolunteer(volunteer1.getVolunteerChatId());
        assertFalse(connectService.freeVolunteers(connectService.getVolunteersList()));
    }
}