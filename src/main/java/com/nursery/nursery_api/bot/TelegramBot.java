package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.handler.*;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import com.nursery.nursery_api.service.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Component
public class TelegramBot extends TelegramLongPollingBot {
    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;
    private final PersonRepository personRepository;
    private final NurseryDBService nurseryDBService;

    private final List<NurseryHandler> nurseryHandlerList;
    private final List<VolunteerHandler> volunteerHandlers;
    private final List<VolunteerCommandHandler> volunteerCommandHandlers;
    private final List<ReportHandler> reportHandlers;
    private final  List<DataReportHandler> dataReportHandlers;
    private final List<RegisterHandler> registerHandlers;

    private final SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(this);
    private final ConnectService connectService;
    private final ReportService reportService;

    private final VolunteerService volunteerService;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(DataReportRepository dataReportRepository,
                       ReportRepository reportRepository,
                       PersonRepository personRepository,
                       NurseryDBService nurseryDBService,
                       List<NurseryHandler> nurseryHandlerList,
                       List<VolunteerHandler> volunteerHandlers,
                       List<VolunteerCommandHandler> volunteerCommandHandlers,
                       List<ReportHandler> reportHandlers, List<DataReportHandler> dataReportHandlers, List<RegisterHandler> registerHandlers, ConnectService connectService,
                       ReportService reportService, VolunteerService volunteerService) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.nurseryDBService = nurseryDBService;
        this.nurseryHandlerList = nurseryHandlerList;
        this.volunteerHandlers = volunteerHandlers;
        this.volunteerCommandHandlers = volunteerCommandHandlers;
        this.reportHandlers = reportHandlers;
        this.dataReportHandlers = dataReportHandlers;
        this.registerHandlers = registerHandlers;
        this.connectService = connectService;
        this.reportService = reportService;
        this.volunteerService = volunteerService;
    }

    //    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && !update.getMessage().hasPhoto() && volunteerService.isVolunteer(update.getMessage().getChatId())) {
            if (update.getMessage().getText().equals("Меню")) {
                checkMessage("-mainVolunteer",update.getMessage().getChatId());
                return;
            }
        }

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            Chat chat = message.getChat();
            // проверяем отчет, если есть фото, значит это отчет
                if (reportService.containPersonForReport(chat.getId())) {
                    saveToDB(chat, message, update, nurseryDBService.getVisitors().get(chat.getId()));
                    reportService.deletePersonForReport(chat.getId());   // удаляем из списка
                } else {
                    sendSimpleText(chat.getId(), "Фото можно присылать только если вы выбрали в меню - 'Отправить отчет'");
                }

        }

            if (update.hasMessage() && update.getMessage().hasText()) {
                if (!update.getMessage().getText().isEmpty()) {
                    if (checkRegistration(update.getMessage().getChatId(), update.getMessage().getText())) {
                        return;
                    }
                    String message = "-main";
                    Long chatIdUser = update.getMessage().getChatId();

                    communicationWithVolunteer(chatIdUser, update, message);

                }
                // проверяем ответы от кнопок
            } else if (update.hasCallbackQuery()) {
                String message = update.getCallbackQuery().getData();
                Long idChat = update.getCallbackQuery().getMessage().getChatId();
                checkDataReport(message, idChat,update);
                checkMessage(message, idChat);
            }
    }

    private boolean checkRegistration(Long chatId, String message) {
        for (var element : registerHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this, nurseryDBService);
               return true;
            }
        }
        return false;
    }


    @Override
    public String getBotUsername() {
        return "animal-shelter-test";
    }

    @Override
    public String getBotToken() {
        return token;
    }

//    private void checkCommandVolunteer(String message, Long chatId){
//
//    }


    /**
     * @param message - строка берется из CallbackQuery. Это значение, что лежит "под кнопкой"
     * @param chatId
     */
    private void checkMessage(String message, Long chatId) {
        for (var element : nurseryHandlerList) {
            if (element.supply(message)) {
                element.handle(chatId, this, nurseryDBService, sendBotMessageService);
                break;
            }
        }

        for (var element : volunteerHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this, connectService);
                break;
            }
        }

        for (var element : reportHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this,reportService, nurseryDBService, sendBotMessageService,connectService);
                break;
            }
        }
    }

    private void checkDataReport(String message, Long chatId, Update update){
        for (var element : dataReportHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this,update,reportService, sendBotMessageService);
                break;
            }
        }
    }

    /**
     * @param message         - строк приходит от волонтера
     * @param chatIdVolunteer Проверяем является ли эта строка командой.
     */
    private void checkVolunteerOperation(String message, Long chatIdVolunteer) {
        for (var element : volunteerCommandHandlers) {
            if (element.supply(message)) {
                element.handle(chatIdVolunteer, this, connectService);
                break;
            }
        }
    }

    /**
     * @param chatId
     * @param message Отправляем стандартное сообщение через бот пользователю
     */
    private void sendSimpleText(Long chatId, String message) {
        try {
            this.execute(SendMessage.
                    builder().
                    chatId(chatId).
                    text(message).
                    build());
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }

    }

    /**
     * сохраняем фото и сообщение из caption отдельно в БД
     * @param chat
     * @param message
     * @param update
     */
    private void saveToDB(Chat chat, Message message, Update update, String nameNursery) {
        String negativeReport="Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так " +
                "подробно, как необходимо. Пожалуйста, подойди ответственнее к " +
                "этому занятию. В противном случае волонтеры приюта будут обязаны " +
                "самолично проверять условия содержания животного";


        Optional<DataReport> dataReport = dataReportRepository.findDataReportByIdChatAndDateNow(chat.getId(), LocalDate.now(),nameNursery);
        if (dataReport.isPresent()) {
            DataReport dataReport1 = dataReport.get();

            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);

            String messageCaption = update.getMessage().getCaption();
            if (messageCaption==null) {
                sendSimpleText(chat.getId(), negativeReport);
                return;
            }

            GetFile getFile = new GetFile();
            getFile.setFileId(photo.getFileId());

            File file = null;

            try {
                file = this.execute(getFile);
            } catch (TelegramApiException e) {
                e.printStackTrace();
            }


            try (InputStream is = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath()).openStream();) {
                dataReport1.setFoto(is.readAllBytes());
                dataReport1.setDateReport(LocalDate.now());
                dataReport1.setMessagePerson(messageCaption);
                dataReport1.setFileSize(file.getFileSize());

            } catch (IOException e) {
                e.printStackTrace();
            }

            dataReportRepository.save(dataReport1);
            sendSimpleText(chat.getId(), "Ваш отчет отправлен!");
        } else {
            sendSimpleText(chat.getId(), "Вы не надены в базе " + nameNursery + ". Выберите в меню другой питомник. Или свяжитесь с волонтером");
        }
    }

    /**
     * общение с волонтером
     * @param chatIdUser
     * @param update
     * @param message
     */
    private void communicationWithVolunteer(long chatIdUser, Update update, String message){
        if (connectService.containInActiveDialog(chatIdUser)) {   // является ли chat id участником активной беседы?
            if (!connectService.isPerson(chatIdUser)) {           // chat id - это волонтер?
                if (Objects.equals(update.getMessage().getText(), "Конец")) {
                    checkVolunteerOperation("Конец",chatIdUser);
                    return;
                }
            }

            if (connectService.isPerson(chatIdUser)) {    // если chat id вопрошающий, то шлем сообщение волонтеру
                sendSimpleText(connectService.getVolunteerChatIdByPersonChatId(chatIdUser), update.getMessage().getText());

            } else {                                        // если нет, то наоборот
                sendSimpleText(connectService.getPersonChatIdByChatIdVolunteer(chatIdUser), update.getMessage().getText());
            }

        } else {

            // если пользователь впервые
            if (!nurseryDBService.contain(chatIdUser)) {
                sendSimpleText(update.getMessage().getChatId(), "Здравствуйте, это питомник домашних животных!");
            }
            checkMessage(message, chatIdUser);       // При любой непонятной команде выводим главное меню чата
        }
        // Регистрация волонтера. Тут же id_chat попадает в базу
        if (update.getMessage().getText().startsWith("Хочу стать волонтером")) {
            Volunteer volunteer = new Volunteer();
            volunteer.setBusy(false);
            volunteer.setVolunteerChatId(chatIdUser);
            connectService.addNewVolunteer(volunteer);
        }
        if (update.getMessage().getText().startsWith("Я ухожу")) {
            connectService.iAmGonnaWayVolunteer(chatIdUser);
        }
    }
}






