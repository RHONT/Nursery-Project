package com.nursery.nursery_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Entity
@Table(name = "data_report")
public class DataReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_report")
    private LocalDate dateDatas;

    @Column(name = "foto")
    @Lob
    private byte[] foto;

    @Column(name = "message_person")
    private String messagePerson;

    @Column(name = "check")
    private boolean check;

    @JsonIgnore
    @ToString.Exclude
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "id_report")
    private Report report;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        DataReport that = (DataReport) o;
        return dateDatas != null && Objects.equals(dateDatas, that.dateDatas);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    public LocalDate getDateDatas() {
        return dateDatas;
    }

    public void setDateDatas(LocalDate dateDatas) {
        this.dateDatas = dateDatas;
    }

    public byte[] getFoto() {
        return foto;
    }

    public void setFoto(byte[] foto) {
        this.foto = foto;
    }

    public String getMessagePerson() {
        return messagePerson;
    }

    public void setMessagePerson(String messagePerson) {
        this.messagePerson = messagePerson;
    }

    public Report getReport() {
        return report;
    }

    public void setReport(Report report) {
        this.report = report;
    }

    public boolean isCheck() {
        return check;
    }

    public void setCheck(boolean check) {
        this.check = check;
    }
}
