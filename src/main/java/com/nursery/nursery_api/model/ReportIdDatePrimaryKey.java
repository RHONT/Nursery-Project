package com.nursery.nursery_api.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDate;


@Data
@NoArgsConstructor
@EqualsAndHashCode
public class ReportIdDatePrimaryKey implements Serializable {
    private Report report;
    private LocalDate dateReport;


}
