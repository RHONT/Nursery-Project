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
@Builder
@Setter
@Getter
@Entity
@Table(name = "person")
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_person")
    private Long idPerson;

//    @OneToOne
//    private Nursery nursery;
    @OneToMany
    @JoinColumn(name = "id_nursery")
    private List<Nursery> nurseryList;

    @Column(name = "id_chat")
    private Long idChat;

    @Column(name = "name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Person person = (Person) o;
        return idPerson != null && Objects.equals(idPerson, person.idPerson);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }


}
