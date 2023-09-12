package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.NurseryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class VolunteerService {
    VolunteerRepository volunteerRepository;

    public VolunteerService(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }
    Logger logger = LoggerFactory.getLogger(VolunteerService.class);

    public Volunteer addVolunteer (Volunteer volunteer){
        logger.info("Вызван метод addVolunteer");
        return volunteerRepository.save(volunteer);
    }

    public Volunteer findVolunteerByName(String name){
        logger.info("Вызван метод findVolunteerByName");
        return volunteerRepository.findByName(name);
    }
    public Volunteer findByPhone (String phone){
        logger.info("Вызван метод findByPhone");
        return volunteerRepository.findByPhone(phone);
    }
    public List<Volunteer> findAllVolunteers (){
        logger.info("Вызван метод findAllVolunteers");
        return volunteerRepository.findAll();
    }
    public Volunteer editVolunteer (Volunteer volunteer){
        logger.info("Вызван метод editVolunteer");
        return volunteerRepository.save(volunteer);
    }

    public Volunteer deleteVolunteerByName (String volunteerName){
        logger.info("Вызван метод deleteVolunteerByName");
        return volunteerRepository.deleteVolunteerByName(volunteerName);
    }

}