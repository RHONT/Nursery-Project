package com.nursery.nursery_api.model;

import lombok.*;

import javax.persistence.*;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "volunteers")
public class Volunteer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long volunteerId;

    @Column(name = "volunteer_name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "telegram_name")
    private String telegramName;

    @Column(name = "free")
    private boolean free;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer)) return false;
        Volunteer volunteer = (Volunteer) o;
        return free == volunteer.free && Objects.equals(volunteerId, volunteer.volunteerId) && Objects.equals(name, volunteer.name) && Objects.equals(phone, volunteer.phone) && Objects.equals(telegramName, volunteer.telegramName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volunteerId);
    }
}