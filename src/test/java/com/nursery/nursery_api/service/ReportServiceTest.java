package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import static com.nursery.nursery_api.global.GlobalVariable.volunteersList;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReportServiceTest {

    @Mock
    private DataReportRepository dataReportRepository;

    @InjectMocks
    private ReportService reportService;

    private static DataReport data1;
    private static DataReport data2;

    private static final Volunteer volunteer1 = Volunteer.builder().
            volunteerId(1L).
            volunteerChatId(111L).
            name("Иван").phone("+7").
            telegramName("@Sobaka").
            busy(false).
            build();

    private static final Volunteer volunteer2 = Volunteer.builder().
            volunteerId(2L).
            volunteerChatId(222L).
            name("Гена").phone("+7").
            telegramName("@Pes").
            busy(true).
            build();


    @BeforeEach
    private void init() {
        data1 = DataReport.builder().
                idDataReport(1L).
                report(new Report()).
                dateReport(LocalDate.now()).
                mediaType("jpg").
                checkMessage(false).
                messagePerson("Тест1").
                fileSize(111L).
                foto(new byte[]{1, 2, 3, 4, 5, 6, 7})
                .build();

        data2 = DataReport.builder().
                idDataReport(1L).
                report(new Report()).
                dateReport(LocalDate.now()).
                mediaType("jpg").
                checkMessage(false).
                messagePerson("Тест1").
                fileSize(111L).
                foto(new byte[]{1, 2, 3, 4, 5, 6, 7})
                .build();
    }

    @Test
    void addNewPersonForReport() {
        reportService.addNewPersonForReport(1L);
        assertTrue(reportService.containPersonForReport(1L));
    }

    @Test
    void deletePersonForReport() {
        reportService.addNewPersonForReport(1L);
        reportService.deletePersonForReport(1L);
        assertFalse(reportService.containPersonForReport(1L));
    }


//    @Test
//    void isReportVolunteer() {
//        volunteersList.put(volunteer1, 0L);
//        volunteersList.put(volunteer2, 1L);
//        assertTrue(reportService.isReportVolunteer(volunteer2.getVolunteerChatId()));
//        assertFalse(reportService.isReportVolunteer(volunteer1.getVolunteerChatId()));
//    }

//    @Test
//    void getOneDataReportGood() {
//        volunteersList.put(volunteer2, 1L);
//        List<DataReport> dataReportList = new ArrayList<>(List.of(data1, data2));
//        when(dataReportRepository.findReportForCheck()).thenReturn(dataReportList);
//        reportService.refreshDataReportQueue();
//        int actual = reportService.getDataReportQueue().size();
//        int expected = 2;
//        assertEquals(expected, actual);
//        reportService.getOneDataReport();
//        assertEquals(1, reportService.getDataReportQueue().size());
//    }

    @Test
    void getOneDataReportOneReportIsVeryBad() {
        volunteersList.put(volunteer2, 1L);

        data2.setFileSize(null);
        data2.setMessagePerson(null);

        DataReport data3=new DataReport();

        List<DataReport> dataReportList = new ArrayList<>(List.of(data1, data2,data3));

        when(dataReportRepository.findReportForCheck()).thenReturn(dataReportList);

        reportService.refreshDataReportQueue();

        int actual = reportService.getDataReportQueue().size();
        int expected = 1;

        assertEquals(expected, actual);

        assertEquals(data1, reportService.getOneDataReport());
    }

    @Test
    void getOneDataReportNotVolunteersForWorkAtReport() {
        List<DataReport> dataReportList = new ArrayList<>(List.of(data1, data2));

        reportService.reportModeDisable(volunteer2.getVolunteerChatId());

        when(dataReportRepository.findReportForCheck()).thenReturn(dataReportList);

        reportService.refreshDataReportQueue();

        DataReport oneDataReport1 = reportService.getOneDataReport();

        assertNull(oneDataReport1.getFileSize());
        assertNull(oneDataReport1.getMessagePerson());
    }

    @Test
    void getOneDataReportEmptyList() {
        List<DataReport> dataReportList = new ArrayList<>();

        reportService.reportModeActive(volunteer2.getVolunteerChatId());

        when(dataReportRepository.findReportForCheck()).thenReturn(dataReportList);

        reportService.refreshDataReportQueue();

        DataReport oneDataReport1 = reportService.getOneDataReport();

        assertNull(oneDataReport1.getFileSize());
        assertNull(oneDataReport1.getMessagePerson());

    }

    @Test
    void statistic() {
        volunteersList.put(volunteer1, 0L);
        volunteersList.put(volunteer2, 1L);
        String result = reportService.statistic();
        assertTrue(result.length() > 10);
    }

    @Test
    void reportModeActive() {
        volunteersList.put(volunteer1, 0L);
        reportService.reportModeActive(volunteer1.getVolunteerChatId());
        assertTrue(reportService.isReportVolunteer(volunteer1.getVolunteerChatId()));
    }

    @Test
    void reportModeDisable() {
        volunteersList.put(volunteer1, 0L);
        reportService.reportModeActive(volunteer1.getVolunteerChatId());
        reportService.reportModeDisable(volunteer1.getVolunteerChatId());
        assertFalse(reportService.isReportVolunteer(volunteer1.getVolunteerChatId()));
    }

    @Test
    void reportIsDoneSaveToBd() {
        reportService.reportIsDoneSaveToBd(data1);
        verify(dataReportRepository,times(1)).save(data1);
    }

    @Test
    void reportIsBadNotSaveToBd() {
        reportService.reportIsBadNotSaveToBd(data1);
        assertEquals(1,reportService.getBadReport().size());
    }

}