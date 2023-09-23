package com.nursery.nursery_api.repositiry;

import com.fasterxml.jackson.annotation.OptBoolean;
import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer,Long> {
    Volunteer findByName(String name);
    Volunteer findByPhone (String phone);
    Volunteer deleteVolunteerByName (String name);
    Optional<Volunteer> findByTelegramName(String telegramName);
    void deleteByTelegramName(String telegramName);
    Optional<Volunteer> findByVolunteerChatId(Long chatIdVolunteer);
    List<Volunteer> findVolunteersByBusyFalse ();


}