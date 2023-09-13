package com.nursery.nursery_api.service;

import com.nursery.nursery_api.model.Nursery;
import com.nursery.nursery_api.model.Visitors;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.*;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;


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

    /**
     * key - Volunteer
     * value - chat id human needy
     */
    private final Map<Volunteer,Long> volunteersList =new ConcurrentHashMap<>();

    /**
     * contain all chat id (volunteer and human needy)
     */
    private final Set<Long> dialogs=new ConcurrentSkipListSet<>();


    private final DataReportRepository dataReportRepository;
    private final NurseryRepository nurseryRepository;
    private final PersonRepository personRepository;
    private final PetRepository petRepository;
    private final ReportRepository reportRepository;
    private final VisitorsRepository visitorsRepository;
    private final VolunteerRepository volunteerRepository;

    public NurseryDBService(DataReportRepository dataReportRepository, NurseryRepository nurseryRepository, PersonRepository personRepository, PetRepository petRepository, ReportRepository reportRepository, VisitorsRepository visitorsRepository, VolunteerRepository volunteerRepository) {
        this.dataReportRepository = dataReportRepository;
        this.nurseryRepository = nurseryRepository;
        this.personRepository = personRepository;
        this.petRepository = petRepository;
        this.reportRepository = reportRepository;
        this.visitorsRepository = visitorsRepository;
        this.volunteerRepository = volunteerRepository;
    }

    // Заводим для кэша мапу с посетителями. Она скудная, лишний раз не будем дергать БД,
    @PostConstruct
    private void init(){
        List<Visitors> listFromDBVisitors= visitorsRepository.findAll();
        if (!listFromDBVisitors.isEmpty()) {
            for (var element:listFromDBVisitors) {
                visitors.put(element.getChatId(),element.getNameNursery());
            }
        }

        // Записываем из базы всех волонтеров в оперативную память
        // Так как у них нет пока собеседника ставим в значение null
        List<Volunteer> allVolunteers = volunteerRepository.findAll();
        for (var element:allVolunteers) {
            volunteersList.put(element,null);
        }
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
            Nursery nursery =nurseryRepository.findNurseryByNameNursery(nameNursery);
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

    // связываем свободного волонтера с страждущим человеком
    public void addToDialogsUser(Long chatId){
        Optional<Volunteer> volunteer = volunteersList.keySet().stream().filter(e -> !e.isBusy()).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(true);
            volunteersList.put(volunteer.get(),chatId);
            dialogs.add(chatId);
            dialogs.add(volunteer.get().getVolunteerChatId());
        }
    }

    // волонтер пишет "-к", а значит беседу нужно закончить
    public void disconnect(Long chatIdVolunteer){
        Optional<Volunteer> volunteer=getVolunteerByChatId(chatIdVolunteer);
        if (volunteer.isPresent()) {
            // удаляем из диалогов и волонтера и вопрошающего
            dialogs.remove(volunteersList.get(volunteer.get()));
            dialogs.remove(volunteer.get().getVolunteerChatId());
            // освобождаем волонтера
            volunteer.get().setBusy(false);
        } else throw new NoSuchElementException("В журнале такого волонтера нет!");
    }

    // Если волонтер заканчивает свою смену
    // он пишет в чат бот "Стоп работа"
    public void disappearanceVolunteer(Long chatIdVolunteer){
        // делаем проверку нет ли волонтера в активной беседе
        // если есть, то чистим ее
        if (dialogs.contains(chatIdVolunteer)) {
            disconnect(chatIdVolunteer);
        }
        // ставим волонтеру занятость на true
        Optional<Volunteer> volunteer= volunteersList.keySet().stream().filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        volunteer.ifPresent(volunteerFree-> volunteerFree.setBusy(true));
    }

    // Добавляем в работу нового волонтера
    // пока не принял решение как он будет туда заноситься
    // Пусть будет "- Хочу стать волонтером: @GonzaMy"
    // Нужно прописать проверку в телеграмме на строку формата message.starWith("- Хочу стать волонтером:")
    // Регуляркой достать @GonzaMy и сохранить в базу нового пользователя

    public Volunteer addNewVolunteer(Volunteer volunteer){
        if (volunteer!=null) {
            // заносим товарища в базу
            volunteerRepository.save(volunteer);
            return volunteer;

        } else throw new NoSuchElementException("Волонтер = null!");
    }

    // Если волонтер хочет самовыпилиться
    // например пишет в чат "- Не хочу быть волонтером @GonzaMy"
    // Нужно прописать проверку в телеграмме на строку формата message.starWith("- Не хочу быть волонтером")
    // и вытащить от туда @GonzaMy
    public Volunteer hasLeftVolunteer(String telegramName){
        Optional<Volunteer> volunteer=volunteerRepository.findByTelegramName(telegramName);
        if (volunteer.isPresent()) {
            volunteerRepository.deleteByTelegramName(telegramName);
            // чистим общий чат
            disconnect(volunteer.get().getVolunteerChatId());
            // удаляем из мапы
            volunteersList.remove(volunteer.get());
        } else throw new NoSuchElementException("Волонтера с таким именем нет!");

        return volunteer.get();
    }

    // Когда волонтер выходит на смену, идем базу достаем от туда волонтера и добавляем в оперативную память
    // Но  если он уже есть в оперативной памяти, то просто ставим занятость на false
    // пусть он вводит: "Волонтер работа: @GonzaMe"
    // если нет возвращаем null
    public Volunteer goOnShiftVolunteer(Long chatIdVolunteer){
        Optional<Volunteer> volunteer= volunteersList.keySet().stream().filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).findFirst();
        if (volunteer.isPresent()) {
            volunteer.get().setBusy(false);
            return volunteer.get();
        } else {
            Optional<Volunteer> volunteerFromDB=volunteerRepository.findByVolunteerChatId(chatIdVolunteer);
            if (volunteerFromDB.isPresent()) {
                volunteerFromDB.get().setBusy(false);
                volunteersList.put(volunteerFromDB.get(),null);
                return volunteerFromDB.get();
            } else throw new NoSuchElementException("Вас нет в базе данных! Обратитесь к администратору");
        }
    }

    private Optional<Volunteer> getVolunteerByChatId(Long chatIdVolunteer){
        return volunteersList.
                keySet().
                stream().
                filter(e-> Objects.equals(e.getVolunteerChatId(), chatIdVolunteer)).
                findFirst();
    }










    // TODO: 006, 06.09.2023 занести в базу изменения, если они произошли.
    @PreDestroy
    private void closeApp(){

    }


}
