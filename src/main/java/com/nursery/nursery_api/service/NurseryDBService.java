package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.repositiry.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;



@Service
public class NurseryDBService {
    /**
     * Key - chat_id
     * value - name nursery
     */
    private final Map<Long,String> visitors=new HashMap<>();
    /**
     * key - name nursery
     * value - nursery
     */
    private final Map<String, Nursery> nurseryMap=new HashMap<>();
    private final NurseryRepository nurseryRepository;
    private final VisitorsRepository visitorsRepository;
    private final Logger logger = LoggerFactory.getLogger(ConnectService.class);

    public NurseryDBService(NurseryRepository nurseryRepository, VisitorsRepository visitorsRepository) {
        this.nurseryRepository = nurseryRepository;
        this.visitorsRepository = visitorsRepository;
    }

    // Заводим для кэша мапу с посетителями. Она скудная, лишний раз не будем дергать БД,
    @PostConstruct
    private void init(){
        logger.info("Вызван метод init");
        List<Visitors> listFromDBVisitors= visitorsRepository.findAll();
        if (!listFromDBVisitors.isEmpty()) {
            for (var element:listFromDBVisitors) {
                visitors.put(element.getChatId(),element.getNameNursery());
            }
        }
    }

    /**
     *
     * @param chat_id - проверяем пользователя, был ли он раньше.
     *                Если не был, заносим его в базу, значения питомника будут занесены
     *                после того, как он выберет питомник, а  пока вставляем null
     */
    public boolean contain(Long chat_id){
        logger.info("Вызван метод contain с параметром {}.", chat_id);
        if (!visitors.containsKey(chat_id)) {
            visitors.put(chat_id,null);
            return false;
        }
        return true;
    }

    /**
     * Когда получили ответ о питомнике, помещаем в мапу значение
     * @param chatId - чат пользователя
     * @param nameNursery - название приюта из колонки БД name_nursery
     */
    public void setNurseryIntoVisitors(Long chatId, String nameNursery){
        logger.info("Вызван метод setNurseryIntoVisitors с параметрами {} и {}.", chatId,nameNursery);
        if (!nurseryMap.containsKey(nameNursery)) {
            Nursery nursery =nurseryRepository.findByNameNursery(nameNursery);
            if (nursery !=null) {
                nurseryMap.put(nameNursery, nursery);
            }
        }
        visitors.put(chatId,nurseryMap.get(nameNursery).getNameNursery());
    }

    /**
     *
     * @param idChat - чат id
     * @return значение "О приюте"
     */
    public String getMeAboutNursery(Long idChat){
        logger.info("Вызван метод getMeAboutNursery с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getAbout();
    }

    /**
     * выдать расписание работы приюта и адрес, схему проезда.
     */
    public String getInfrastructure(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getInfrastructure();
    }

    /**
     * общие рекомендации о технике безопасности на территории приюта.
     */
    public String getAccident_prevention(Long idChat){
        logger.info("Вызван метод getAccident_prevention с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getAccidentPrevention();
    }

    /**
     * список документов, необходимых для того, чтобы взять животное из приюта.
     */
    public String getDocument(Long idChat){
        logger.info("Вызван метод getDocument с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getListDocument();
    }

    /**
     * список документов, необходимых для того, чтобы взять животное из приюта.
     */
    public String getHowGetPetFromNursery(Long idChat){
        logger.info("Вызван метод getHowGetPetFromNursery с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getHowGetPet();
    }

    /**
     * правила транспортировки животного
     */
    public String getTransportRule(Long idChat){
        logger.info("Вызван метод getTransportRule с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getTransportRule();
    }

    /**
     * правила обустройства дома для котят|щенят
     */
    public String getHouseRecommendForBabyPet(Long idChat){
        logger.info("Вызван метод getHouseRecommendForBabyPet с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getHouseRecomendBaby();
    }

    /**
     * правила обустройства дома для взрослых кошек/собак
     */
    public String getHouseRecommendForAdultPet(Long idChat){
        logger.info("Вызван метод getHouseRecommendForAdultPet с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getHouseRecomendAdult();
    }

    /**
     * правила обустройства дома для животных с ограничениями
     */
    public String getHouseRecommendInvalid(Long idChat){
        logger.info("Вызван метод getHouseRecommendInvalid с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getHouseRecommendInvalid();
    }

    /**
     * перваначальные советы кинолога
     */
    public String getСynologistAdvice(Long idChat){
        logger.info("Вызван метод getСynologistAdvice с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getCynologistAdvice();
    }

    /**
     * продвинутые советы кинолога
     */
    public String getСynologistAdviceUp(Long idChat){
        logger.info("Вызван метод getСynologistAdviceUp с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getCynologistAdviceUp();
    }

    /**
     * причины отказа
     */
    public String getReasonsRefusal(Long idChat){
        logger.info("Вызван метод getReasonsRefusal с параметром {}.", idChat);
        return nurseryMap.get(visitors.get(idChat)).getReasonsRefusal();
    }

    public Map<Long, String> getVisitors() {
        logger.info("Вызван метод getVisitors.");
        return visitors;
    }

    // TODO: 006, 06.09.2023 занести в базу изменения, если они произошли.
    @PreDestroy
    private void closeApp(){

    }


}
