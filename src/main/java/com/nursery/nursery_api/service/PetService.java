package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Pet;
import com.nursery.nursery_api.repositiry.PetRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PetService {
    PetRepository petRepository;
    NurseryService nurseryService;

    public PetService(PetRepository petRepository) {
        this.petRepository = petRepository;
    }
    Logger logger = LoggerFactory.getLogger(NurseryService.class);

    public Pet addPet (Pet pet){
        logger.info("Вызван метод addPet");
        return petRepository.save(pet);
    }
    public List<Pet> findAll (){
        logger.info("Вызван метод findAll");
        return petRepository.findAll();
    }
    public Pet findPetByName (String petName){
        logger.info("Вызван метод findPetByName");
        return petRepository.findPetByNickname(petName);
    }
    public List<Pet> findPetsByNurseryName(String nurseryName){
        logger.info("Вызван метод findPetsByNurseryName(");
        List<Pet>allNurseryPets;
        Long nurseryId = nurseryService.getNurseryIdByName(nurseryName);
        allNurseryPets = petRepository.findPetsByNurseryId(nurseryId);
        return allNurseryPets;
    }
    public List<Pet> findPetsByInvalid (){
        logger.info("Вызван метод findPetsByInvalid");
        return petRepository.findPetsByInvalidTrue();
    }
    public List<Pet> findHealthyPets (){
        logger.info("Вызван метод findHealthyPets");
        return petRepository.findPetsByInvalidFalse();
    }
    public Pet editPet(Pet pet){
        logger.info("Вызван метод editPet");
        return petRepository.save(pet);
    }

    public Pet deletePetByName(String petName){
        logger.info("Вызван метод deletePetByName");
        return petRepository.deletePetByNickname(petName);
    }

}