package com.nursery.nursery_api.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "data_report")
public class DataReport {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "date_datas")
    private LocalDate dateDatas;

    @Column(name = "foto")
    @Lob
    private byte[] foto;

    @Column(name = "diet")
    private String diet;

    @Column(name = "health")
    private String health;

    @Column(name = "demeanor")
    private String demeanor;

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
}
