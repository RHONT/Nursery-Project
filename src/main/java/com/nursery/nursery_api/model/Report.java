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
@Setter
@Getter
@Builder
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
    private Integer dayReport;

    @JsonIgnore
    @OneToMany(mappedBy = "report",fetch = FetchType.EAGER)
    @ToString.Exclude
    private List<DataReport> dataReports;

//    public List<DataReport> getDataReports() {
//        if (dataReports==null) {
//            dataReports=new ArrayList<>();
//        }
//        return dataReports;
//    }

    public void addDataReports(DataReport dataReport) {
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


}
