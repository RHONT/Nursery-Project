package com.nursery.nursery_api.repositiry;

import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.model.Volunteer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VolunteerRepository extends JpaRepository<Volunteer,Long> {
    Volunteer findByName(String name);
    Volunteer findByPhone (String phone);
    Volunteer deleteVolunteerByName (String name);
}