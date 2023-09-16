package com.nursery.nursery_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Setter
@Getter
@Entity
@IdClass(ReportIdDatePrimaryKey.class)
@Table(name = "data_report")
public class DataReport implements Serializable {

//    @JsonIgnore
//    @ManyToOne(fetch = FetchType.LAZY)
//    @JoinColumn(name = "id_nursery", nullable = true)
//    private Nursery nursery;

    @Id
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_report", nullable=false)
    private Report report;

    @Id
    @Column(name = "date_report", nullable=false)
    private LocalDate dateReport;

//    @JsonIgnore
//    @ToString.Exclude
//    @ManyToOne(fetch = FetchType.EAGER)
//    @JoinColumn(name = "id_report")
//    private Report report;

    @Column(name = "foto")
    @Lob
    private byte[] foto;

    @Column(name = "message_person")
    private String messagePerson;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "check")
    private boolean check;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DataReport that = (DataReport) o;
        return report != null && Objects.equals(report, that.report)
                && dateReport != null && Objects.equals(dateReport, that.dateReport);
    }

    @Override
    public int hashCode() {
        return Objects.hash(report, dateReport);
    }
}
