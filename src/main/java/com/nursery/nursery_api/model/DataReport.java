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
@Builder
@Entity
@Table(name = "data_report")
public class DataReport{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_data_report")
    private Long idDataReport;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_report", nullable=false)
    private Report report;

    @Column(name = "date_report", nullable=false)
    private LocalDate dateReport;

    @Column(name = "foto")
    @Lob
    private byte[] foto;

    @Column(name = "message_person")
    private String messagePerson;

    @Column(name = "file_size")
    private Long fileSize;

    @Column(name = "media_type")
    private String mediaType;

    @Column(name = "check_message")
    private boolean checkMessage;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DataReport that = (DataReport) o;
        return idDataReport != null && Objects.equals(idDataReport, that.idDataReport);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public Report getReport() {
        return report;
    }

    public boolean isCheckMessage() {
        return checkMessage;
    }

    public void setCheckMessage(boolean checkMessage) {
        this.checkMessage = checkMessage;
    }
}
