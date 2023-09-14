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
@Setter
@Getter
@Entity
@Builder
@Table(name = "nursery")
public class Nursery {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_nursery")
    private Long idNursery;

    @Column(name = "name_nursery")
    private String nameNursery;

    @Column(name = "about")
    private String about;

    @Column(name = "infrastructure")
    private String infrastructure;

    @Column(name = "accident_prevention")
    private String accidentPrevention;

    @Column(name = "how_get_pet")
    private String howGetPet;

    @Column(name = "dating_rule")
    private String datingRule;

    @Column(name = "transport_rule")
    private String transportRule;

    @Column(name = "house_recommend_baby")
    private String houseRecomendBaby;

    @Column(name = "house_recommend_adult")
    private String houseRecomendAdult;

    @Column(name = "house_recommend_invalid")
    private String houseRecommendInvalid;

    @Column(name = "cynologist_advice")
    private String cynologistAdvice;

    @Column(name = "cynologist_advice_up")
    private String cynologistAdviceUp;

    @Column(name = "reasons_refusal")
    private String reasonsRefusal;

    @Column(name = "list_document")
    private String listDocument;

    @JsonIgnore
    @OneToMany(mappedBy = "nursery")
    private List<Pet> pets = new ArrayList<>();

    public void AddPet(Pet pet) {
        if (pets==null) {
            pets=new ArrayList<>();
        }
        pets.add(pet);
        pet.setNursery(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Nursery nursery = (Nursery) o;
        return idNursery != null && Objects.equals(idNursery, nursery.idNursery);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

}
