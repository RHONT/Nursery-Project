package com.nursery.nursery_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "report")
public class Report {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_report")
    private Long idReport;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "id_person")
    private Person person;


    @Column(name = "forteit")
    private Long forteit;

    @Column(name = "day_report")
    private Long dayReport;

    @JsonIgnore
    @OneToMany(mappedBy = "report")
    @ToString.Exclude
    private List<DataReport> dataReports;

    public List<DataReport> getDataReports() {
        if (dataReports==null) {
            dataReports=new ArrayList<>();
        }
        return dataReports;
    }

    public void AddDataReports(DataReport dataReport) {
        if (dataReports==null) {
            dataReports=new ArrayList<>();
        }
        dataReports.add(dataReport);
        dataReport.setReport(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Report report = (Report) o;
        return idReport != null && Objects.equals(idReport, report.idReport);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Long getIdReport() {
        return idReport;
    }

    public void setIdReport(Long idReport) {
        this.idReport = idReport;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Long getForteit() {
        return forteit;
    }

    public void setForteit(Long forteit) {
        this.forteit = forteit;
    }

    public Long getDayReport() {
        return dayReport;
    }

    public void setDayReport(Long dayReport) {
        this.dayReport = dayReport;
    }

    public void setDataReports(List<DataReport> dataReports) {
        this.dataReports = dataReports;
    }
}
