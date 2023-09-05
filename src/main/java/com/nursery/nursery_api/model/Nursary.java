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
@Getter
@Setter
@ToString
@Entity
@Table(name = "nursary")
public class Nursary {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nursary")
    private Long idNursary;

    @Column(name = "name_nursary")
    private String nameNursary;

    @Column(name = "about")
    private String about;

    @Column(name = "infrastructure")
    private String infrastructure;

    @Column(name = "accident_prevention")
    private String accidentPrevention;

    @Column(name = "how_get_pet")
    private String howGetPet;

    @Column(name = "list_document")
    private String listDocument;

    @JsonIgnore
    @OneToMany(mappedBy = "nursary")
    private List<Pet> pets = new ArrayList<>();

    public void AddPet(Pet pet) {
        if (pets==null) {
            pets=new ArrayList<>();
        }
        pets.add(pet);
        pet.setNursary(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Nursary nursary = (Nursary) o;
        return idNursary != null && Objects.equals(idNursary, nursary.idNursary);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
