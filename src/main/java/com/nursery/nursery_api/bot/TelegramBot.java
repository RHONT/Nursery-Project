package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.handler.VolunteerCommandHandler;
import com.nursery.nursery_api.handler.VolunteerHandler;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import com.nursery.nursery_api.service.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.List;
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
    private final SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(this);
    private final ConnectService connectService;
    private final ReportService reportService;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(DataReportRepository dataReportRepository,
                       ReportRepository reportRepository,
                       PersonRepository personRepository,
                       NurseryDBService nurseryDBService,
                       List<NurseryHandler> nurseryHandlerList,
                       List<VolunteerHandler> volunteerHandlers,
                       List<VolunteerCommandHandler> volunteerCommandHandlers,
                       List<ReportHandler> reportHandlers, ConnectService connectService,
                       ReportService reportService) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.nurseryDBService = nurseryDBService;
        this.nurseryHandlerList = nurseryHandlerList;
        this.volunteerHandlers = volunteerHandlers;
        this.volunteerCommandHandlers = volunteerCommandHandlers;
        this.reportHandlers = reportHandlers;
        this.connectService = connectService;
        this.reportService = reportService;
    }

    //    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage() && update.getMessage().hasPhoto()) {
            Message message = update.getMessage();
            Chat chat = message.getChat();
            // проверяем отчет, если есть фото, значит это отчет
            if (message.hasPhoto()) {

                if (reportService.containPersonForReport(chat.getId())) {
                    saveToDB(chat, message, update, nurseryDBService.getVisitors().get(chat.getId()));
                    reportService.deletePersonForReport(chat.getId());   // удаляем из списка
                } else {
                    sendSimpleText(chat.getId(), "Фото можно присылать только если вы выбрали в меню - 'Отправить отчет'");
                }
            }
        }

            if (update.hasMessage() && update.getMessage().hasText()) {
                if (!update.getMessage().getText().isEmpty()) {
                    String message = "-main";
                    Long chatIdUser = update.getMessage().getChatId();

                    communicationWithVolunteer(chatIdUser, update, message);

                }
                // проверяем ответы от кнопок
            } else if (update.hasCallbackQuery()) {
                String message = update.getCallbackQuery().getData();
                Long idChat = update.getCallbackQuery().getMessage().getChatId();
                checkMessage(message, idChat);
            }
    }


    @Override
    public String getBotUsername() {
        return "animal-shelter-test";
    }

    @Override
    public String getBotToken() {
        return token;
    }

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
                element.handle(chatId, this,reportService, nurseryDBService, sendBotMessageService);
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
        Optional<DataReport> dataReport = dataReportRepository.findDataReportByIdChatAndDateNow(chat.getId(), LocalDate.now(),nameNursery);
        if (dataReport.isPresent()) {
            DataReport dataReport1 = dataReport.get();

            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);

            String messageCaption = update.getMessage().getCaption();

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
            sendSimpleText(chat.getId(), "Вы на надены в базе " + nameNursery + "Выберите в меню другой питомник. Или свяжитесь с волонтером");
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
                String checkedMessage = update.getMessage().getText();
                checkVolunteerOperation(checkedMessage, chatIdUser);
                return;
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


// Дальше описана логика как мы достаем картинку из базы
// и отправляем нужному пользователю.

//    SendPhoto sendPhoto = new SendPhoto();
//
//    DataReport dataReportReturn = dataReportRepository.findByReportAndDateReport(report, LocalDate.now()).get();
//    byte[] foto = dataReportReturn.getFoto();
//                sendPhoto.setChatId(update.getMessage().getChatId());
//
//                        InputFile inputFile = new InputFile(new ByteArrayInputStream(foto), "photo.jpg");
//                        sendPhoto.setPhoto(inputFile); // Установка фотографии в объекте SendPhoto
//                        try {
//                        this.execute(sendPhoto);
//                        } catch (TelegramApiException e) {
//                        e.printStackTrace();
//                        }

