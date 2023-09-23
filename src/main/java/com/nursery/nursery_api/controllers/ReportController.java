package com.nursery.nursery_api.controllers;


import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Person;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.service.ReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/nursery_app/admin_functions/reports")
public class ReportController {
    private ReportService reportService;
    public ReportController(ReportService reportService) {
        this.reportService = reportService;
    }

    @Operation(summary = "Добавление системы отчетов к посетителю.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленная к посетителю системы отчетов.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            tags = "Report"
    )
    @PostMapping(path = "/start_nursing_for_pet")
    public ResponseEntity<Report> addReport (@RequestBody Report report){
        return ResponseEntity.ok(reportService.addNewReportForPerson(report));
    }

    @Operation(summary = "Поиск системы отчетов по посетителю или по одному из ежедневных расчетов. Или вывод всех " +
            "присоединенных к посетителям систем отчета.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Найденная система отчетов.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            tags = "Report"
    )
    @GetMapping(path = "/find_report")
    public ResponseEntity<Object> findReport (@RequestParam(required = false)Long personId){
        if(personId != null){
            return ResponseEntity.ok(reportService.findReportInfoForPersonId(personId));
        }
        return ResponseEntity.ok(reportService.findAll());
    }

    @Operation(summary = "Изменение аспектов системы отчетов посетителя.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Изменение системы отчетов.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Новая версия объекта класса Report.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class)
                    )
            ),
            tags = "Report"
    )
    @PutMapping(path = "/edit_report")
    public ResponseEntity<Report> editReport (@RequestBody Report report){
        return ResponseEntity.ok(reportService.editReport(report));
    }

    @Operation(summary = "Удаление системы отчетов связанной с посетителем по самому объекту Report или его Id.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Добавленная к посетителю системы отчетов.",
                            content = @Content(
                                    mediaType = MediaType.APPLICATION_JSON_VALUE,
                                    array = @ArraySchema(schema = @Schema(implementation = Report.class))
                            )
                    )
            },
            requestBody = @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = "Удаляемый объект класса Report.",
                    content = @Content(
                            mediaType = MediaType.APPLICATION_JSON_VALUE,
                            schema = @Schema(implementation = Report.class)
                    )
            ),
            tags = "Report"
    )
    @DeleteMapping(path = "/delete_report")
    public ResponseEntity<?> deleteReportByIdOrReport (@RequestParam(required = false) Long reportId,
                                                    @RequestBody(required = false) Report report){
        if (reportId != null) {
            try {
                return ResponseEntity.ok(reportService.deleteReportByReportId(reportId));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        }
        if (report != null){
            try {
                return ResponseEntity.ok(reportService.deleteReportByReportId(report.getIdReport()));
            } catch (Exception e) {
                e.printStackTrace();
                return ResponseEntity.badRequest().build();
            }
        }
        return ResponseEntity.notFound().build();
    }
}
