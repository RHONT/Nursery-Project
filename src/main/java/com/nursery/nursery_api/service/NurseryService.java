package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.repositiry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;

import java.util.List;



@Service
public class NurseryService {
    private final NurseryRepository nurseryRepository;


    Logger logger = LoggerFactory.getLogger(NurseryService.class);

    public NurseryService(NurseryRepository nurseryRepository) {
        this.nurseryRepository = nurseryRepository;
    }

    /** Method that realise adding new object Nursery
     * Use repository method {@link JpaRepository#save(Object)}
     * @param nursery
     * @return added Nursary
     */
    public Nursery createNursery(Nursery nursery){
        logger.info("Вызван метод createNursery");
        return nurseryRepository.save(nursery);
    }

    /** Method that find all Nurseries and return them as List
     * Use repository method {@link JpaRepository#findAll()}
     * @return List<Nursery>
     */
    public List<Nursery> getAllNursery (){
        logger.info("Вызван метод getAllNursery");
        return nurseryRepository.findAll();
    }

    /** Method that find Object Nursery by name of nursery
     *
     * @param nurseryName
     * @return Nursery object
     */
    public Nursery findNurseryByName(String nurseryName){
        logger.info("Вызван метод findNurseryByName с параметром {}", nurseryName);
        return nurseryRepository.findByNameNursery(nurseryName);
    }

    /** Method that find nursery's id by there name
     *
     * @param nurseryName
     * @return nurseries Id
     */
    public Long getNurseryIdByName (String nurseryName){
        logger.info("Вызван метод getNurseryIdByName с параметром {}", nurseryName);
        return nurseryRepository.nurseryIdByName(nurseryName);
    }

    /** Method that realise deleting of Nursery object by his name
     *
     * @param nurseryName
     * @return deleted object Nursery
     */
    public Nursery deleteNurseryByName(String nurseryName){
        logger.info("Вызван метод deleteNurseryByName с параметром {}", nurseryName);
        return nurseryRepository.deleteNurseryByNameNursery(nurseryName);
    }

    /** Method that realise editing of Nursery object.
     *
     * @param nursery
     * @return new Nursary object
     */
    public Nursery editNursery (Nursery nursery){
        logger.info("Вызван метод editNursery");
        return nurseryRepository.save(nursery);
    }


}
