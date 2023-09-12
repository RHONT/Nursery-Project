package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.repositiry.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class NurseryService {
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

    private final DataReportRepository dataReportRepository;
    private final NursaryRepository nurseryRepository;
    private final PersonRepository personRepository;
    private final PetRepository petRepository;
    private final ReportRepository reportRepository;
    private final VisitorsRepository visitorsRepository;

    // Заводим для кэша мапу с посетителями. Она скудная, лишний раз не будем дергать БД,
    @PostConstruct
    private void init(){
        List<Visitors> listFromDBVisitors= visitorsRepository.findAll();
        if (!listFromDBVisitors.isEmpty()) {
            for (var element:listFromDBVisitors) {
                visitors.put(element.getChatId(),element.getNameNursery());
            }
        }
    }


    public NurseryService(DataReportRepository dataReportRepository, NursaryRepository nurseryRepository, PersonRepository personRepository, PetRepository petRepository, ReportRepository reportRepository, VisitorsRepository visitorsRepository) {
        this.dataReportRepository = dataReportRepository;
        this.nurseryRepository = nurseryRepository;
        this.personRepository = personRepository;
        this.petRepository = petRepository;
        this.reportRepository = reportRepository;
        this.visitorsRepository = visitorsRepository;
    }

    /**
     *
     * @param chat_id - проверяем пользователя, был ли он раньше.
     *                Если не был, заносим его в базу, значения питомника будут занесены
     *                после того, как он выберет питомник, а  пока вставляем null
     */
    public boolean contain(Long chat_id){
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
        if (!nurseryMap.containsKey(nameNursery)) {
            Nursery nursery =nurseryRepository.findByNameNursary(nameNursery);
            if (nursery !=null) {
                nurseryMap.put(nameNursery, nursery);
            }
        }
        visitors.put(chatId,nurseryMap.get(nameNursery).getNameNursary());
    }

    /**
     *
     * @param idChat - чат id
     * @return значение "О приюте"
     */
    public String getMeAboutNursery(Long idChat){
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
        return nurseryMap.get(visitors.get(idChat)).getAccidentPrevention();
    }

    /**
     * список документов, необходимых для того, чтобы взять животное из приюта.
     */
    public String getDocument(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getListDocument();
    }

    /**
     * список документов, необходимых для того, чтобы взять животное из приюта.
     */
    public String getHowGetPetFromNursery(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getHowGetPet();
    }

    /**
     * правила транспортировки животного
     */
    public String getTransportRule(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getTransportRule();
    }

    /**
     * правила обустройства дома для котят|щенят
     */
    public String getHouseRecommendForBabyPet(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getHouseRecomendBaby();
    }

    /**
     * правила обустройства дома для взрослых кошек/собак
     */
    public String getHouseRecommendForAdultPet(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getHouseRecomendAdult();
    }

    /**
     * правила обустройства дома для животных с ограничениями
     */
    public String getHouseRecommendInvalid(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getHouseRecommendInvalid();
    }

    /**
     * перваначальные советы кинолога
     */
    public String getСynologistAdvice(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getCynologistAdvice();
    }

    /**
     * продвинутые советы кинолога
     */
    public String getСynologistAdviceUp(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getCynologistAdviceUp();
    }

    /**
     * причины отказа
     */
    public String getReasonsRefusal(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getReasonsRefusal();
    }


    // TODO: 006, 06.09.2023 занести в базу изменения, если они произошли.
    @PreDestroy
    private void closeApp(){

    }


}
