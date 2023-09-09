package com.nursery.nursery_api.model.services;

import com.nursery.nursery_api.model.Nursary;
import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.repositiry.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

@Service
public class NurseryService {
    /**
     * Key - chat_id
     * value - name nursery
     */
    private final Map<Long,String> visitors=new HashMap<>();
    /**
     * key - name nursery
     * * value - nursery
     */
    private final Map<String, Nursary> nurseryMap=new HashMap<>();

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
                visitors.put(element.getChatId(),element.getNameNursary());
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
     * @param chatId
     * @param nameNursery
     */
    public void setNurseryIntoVisitors(Long chatId, String nameNursery){
        if (!nurseryMap.containsKey(nameNursery)) {
            Nursary nursary=nurseryRepository.findByNameNursary(nameNursery);
            if (nursary!=null) {
                nurseryMap.put(nameNursery,nursary);
            }
        }
        visitors.put(chatId,nurseryMap.get(nameNursery).getNameNursary());
    }

    /**
     *
     * @param idChat - чат id
     * @return we find the nursery by chat_id from the visitors map and get information about the nursery from there
     */
    public String getMeAboutNursery(Long idChat){
        return nurseryMap.get(visitors.get(idChat)).getAbout();
    }

    /**
     * выдать расписание работы приюта и адрес, схему проезда.
     */
//    public String getInfrastructure(Long chat_id){
//
////        return nurseryRepository.
////                findById(visitors.get(chat_id)).
////                orElseThrow(()->new NoSuchElementException("Приют не найден")).
////                getInfrastructure();
//    }

//    /**
//     * общие рекомендации о технике безопасности на территории приюта.
//     */
//    public String getAccident_prevention(Long chat_id){
//        return nurseryRepository.
//                findById(visitors.get(chat_id)).
//                orElseThrow(()->new NoSuchElementException("Приют не найден")).
//                getAccidentPrevention();
//    }
//
//    /**
//     * список документов, необходимых для того, чтобы взять животное из приюта.
//     */
//    public String getDocument(Long chat_id){
//        return nurseryRepository.
//                findById(visitors.get(chat_id)).
//                orElseThrow(()->new NoSuchElementException("Приют не найден")).
//                getListDocument();
//    }
//
//    /**
//     * список документов, необходимых для того, чтобы взять животное из приюта.
//     */
//    public String getHowGetPetFromNursery(Long chat_id){
//        return nurseryRepository.
//                findById(visitors.get(chat_id)).
//                orElseThrow(()->new NoSuchElementException("Приют не найден")).
//                getHowGetPet();
//    }

//    /**
//     * правила транспортировки животного
//     */
//    public String getTransportRule(Long idNursery){
//        return nurseryRepository.
//                findById(idNursery).
//                orElseThrow(()->new NoSuchElementException("Приют не найден")).
//                getTransportRule();
//    }




    // TODO: 006, 06.09.2023 занести в базу изменения, если они произошли.
    @PreDestroy
    private void closeApp(){

    }


}
