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
@Table(name = "visitors")
public class Visitors {
    @Id
    @Column(name = "chat_id")
    private Long chatId;

    @Column(name = "name_nursery")
    private String nameNursery;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        Visitors visitors = (Visitors) o;
        return chatId != null && Objects.equals(chatId, visitors.chatId);
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
