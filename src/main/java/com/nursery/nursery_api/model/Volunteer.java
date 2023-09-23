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
@Builder
@Table(name = "volunteers")
public class Volunteer {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "volunteer_id")
    private Long volunteerId;

    @Column(name = "volunteer_chat_id")
    private Long volunteerChatId;

    @Column(name = "volunteer_name")
    private String name;

    @Column(name = "phone")
    private String phone;

    @Column(name = "telegram_name")
    private String telegramName;

    @Column(name = "busy")
    private boolean busy;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Volunteer)) return false;
        Volunteer volunteer = (Volunteer) o;
        return busy == volunteer.busy && Objects.equals(volunteerId, volunteer.volunteerId) && Objects.equals(name, volunteer.name) && Objects.equals(phone, volunteer.phone) && Objects.equals(telegramName, volunteer.telegramName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(volunteerId);
    }
}