package com.nursery.nursery_api.controllers;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.repositiry.NurseryRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import com.nursery.nursery_api.service.PersonService;
import com.nursery.nursery_api.service.PetService;
import com.nursery.nursery_api.service.ReportService;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.telegram.telegrambots.meta.TelegramBotsApi;

import java.util.Optional;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class ReportControllerTest {

    @Autowired
    private MockMvc mockMvc;


    @MockBean
    private ReportRepository reportRepository;
    @MockBean
    private PersonRepository personRepository;
    @MockBean
    private TelegramBot telegramBot;
    @MockBean
    private TelegramBotsApi telegramBotsApi;

    @MockBean
    private ReportService reportService;
    @SpyBean
    private PersonService personService;
    @MockBean
    private NurseryRepository nurseryRepository;

    @InjectMocks
    private ReportController reportController;
    @InjectMocks
    private PersonController personController;
    private final Nursery nursery= Nursery.builder().idNursery(3L).nameNursery("Змеи").build();

    private final Person personGreg = Person.builder()
            .idPerson(888L)
            .name("Gregorio")
            .idChat(987654L)
            .phone("123456789")
            .nursery(nursery)
            .build();

    private final Report report = Report.builder()
            .idReport(123L)
            .person(personGreg)
            .forteit(1L)
            .dayReport(30)
            .build();


    @Test
    void addReport() throws Exception {
        JSONObject reportObject = new JSONObject();
        reportObject.put("idReport", report.getIdReport());

        when(reportRepository.save(any(Report.class))).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders
                        .post("/nursery_app/admin_functions/reports/start_nursing_for_pet")
                        .content(reportObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void findReportByPersonId() throws Exception {
        JSONObject reportObject = new JSONObject();
        reportObject.put("idReport", report.getIdReport());

        when(reportRepository.findReportByPersonId(anyLong())).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/nursery_app/admin_functions/reports/find_report?personId=123")
                        .content(reportObject.toString())
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void editReport() throws Exception {

        JSONObject reportObject = new JSONObject();
        reportObject.put("id_report", report.getIdReport());

        when(reportRepository.save(any(Report.class))).thenReturn(report);

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/nursery_app/admin_functions/reports/edit_report")
                        .content(reportObject.toString())
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
    }

    @Test
    void deleteReportByReportId() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/nursery_app/admin_functions/reports/delete_report/"+report.getIdReport())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))

                .andExpect(status().isOk());

        verify(reportService,times(1)).deleteReportByReportId(report.getIdReport());
    }
}