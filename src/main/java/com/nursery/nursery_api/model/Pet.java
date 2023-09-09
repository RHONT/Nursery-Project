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
@Table(name = "pet")
public class Pet {
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_nursery", nullable = true)
    private Nursery nursery;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pet")
    private Long idPet;

    @Column(name = "nickname")
    private String nickname;

    @Column(name = "birthday")
    private LocalDate age;

    @Column(name = "foto")
    @Lob
    private byte[] foto;



    @Column(name = "invalid")
    private Boolean invalid;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "person_id")
    private Person person;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Pet pet = (Pet) o;
        return idPet != null && Objects.equals(idPet, pet.idPet);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
