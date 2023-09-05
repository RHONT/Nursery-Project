package com.nursery.nursery_api.model;

import lombok.*;
import org.hibernate.Hibernate;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "house_recommend_variant")
public class HouseRecommendVariant {
    @Id
    @Column(name = "id_house_recommend_variant")
    private Long idHouseRecommendVariant;

    @Column(name = "type_animal")
    private String typeAnimal;

    @Column(name = "age")
    private Long age;

    @Column(name = "description")
    private String description;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        HouseRecommendVariant that = (HouseRecommendVariant) o;
        return idHouseRecommendVariant != null && Objects.equals(idHouseRecommendVariant, that.idHouseRecommendVariant);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
