package com.nursery.nursery_api.bot;

import com.nursery.nursery_api.handler.NurseryHandler;
import com.nursery.nursery_api.handler.VolunteerCommandHandler;
import com.nursery.nursery_api.handler.VolunteerHandler;
import com.nursery.nursery_api.model.DataReport;
import com.nursery.nursery_api.model.Report;
import com.nursery.nursery_api.repositiry.DataReportRepository;
import com.nursery.nursery_api.repositiry.PersonRepository;
import com.nursery.nursery_api.repositiry.ReportRepository;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.SendBotMessageService;
import com.nursery.nursery_api.service.SendBotMessageServiceImpl;
import lombok.SneakyThrows;

import org.apache.tomcat.util.http.fileupload.IOUtils;
import org.postgresql.core.Oid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.GetFile;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

@Component

public class TelegramBot extends TelegramLongPollingBot {
    private final DataReportRepository dataReportRepository;
    private final ReportRepository reportRepository;
    private final PersonRepository personRepository;

    private final NurseryDBService nurseryDBService;
    private final List<NurseryHandler> nurseryHandlerList;
    private final List<VolunteerHandler> volunteerHandlers;
    private final List<VolunteerCommandHandler> volunteerCommandHandlers;
    private final SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(this);
    private final ConnectService connectService;

    @Value("${telegram.bot.token}")
    private String token;

    public TelegramBot(DataReportRepository dataReportRepository, ReportRepository reportRepository, PersonRepository personRepository, NurseryDBService nurseryDBService,
                       List<NurseryHandler> nurseryHandlerList,
                       List<VolunteerHandler> volunteerHandlers,
                       List<VolunteerCommandHandler> volunteerCommandHandlers,
                       @Lazy ConnectService connectService) {
        this.dataReportRepository = dataReportRepository;
        this.reportRepository = reportRepository;
        this.personRepository = personRepository;
        this.nurseryDBService = nurseryDBService;
        this.nurseryHandlerList = nurseryHandlerList;
        this.volunteerHandlers = volunteerHandlers;
        this.volunteerCommandHandlers = volunteerCommandHandlers;
        this.connectService = connectService;
    }


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {

        if (update.hasMessage()) {
            Message message = update.getMessage();
            Chat chat = message.getChat();
// проверяем отчет, если есть фото, значит это отчет
            // нужно сгененировать это событие на нажатие кнопки прислать отчет
            if (message.hasPhoto()) {

                List<PhotoSize> photos = message.getPhoto();
                PhotoSize photo = photos.get(photos.size() - 1);
                GetFile getFile = new GetFile();
                getFile.setFileId(photo.getFileId());
                File file = this.execute(getFile);
                InputStream is = new URL("https://api.telegram.org/file/bot" + getBotToken() + "/" + file.getFilePath()).openStream();

                // new DataReport() = делал для теста, а нужно
                // Тут нужно написать запрос, чтобы вернулся отчет где стоит сегодняшняя дата
                // по шедулеру каждую ночь будет создаваться пустая запись у каждого, кто проходит испытания
                // То есть нужно найти человека, который есть в базе, найти отчет где он фигурирует и вернуть день этого отчета
                // Так как мы храним в таблице visitors номер питомника, как результат, мы можем точно найти эту запись
                // в таблице Person теперь есть  поле id_nursery
                DataReport dataReport = new DataReport();

                // тут для теста я взял готовую запись у себя
                // в репозитории нужно написать методы:
                // Найти отчет для человека, у человека есть chat_Id
                // удобнее это будет сделать через @Query
                Report report = reportRepository.findById(2L).get();
                //Забиваем сущность dataReport данными
                dataReport.setReport(report);
                // текущая дата
                dataReport.setDateReport(LocalDate.now());
                dataReport.setMessagePerson("Текстовое сообщение от пользователя");
                dataReport.setFileSize(file.getFileSize());
                // записываем фото в базу
                dataReport.setFoto(is.readAllBytes());

                dataReportRepository.save(dataReport);

                // закрываем поток
                is.close();

            }
        }

        // Дальше идет старый рабочий код!

//        if (update.hasMessage() && update.getMessage().hasText()) {
//            if (!update.getMessage().getText().isEmpty()) {
//                String message = "-main";
//                Long chatIdUser = update.getMessage().getChatId();
//
//                if (connectService.containInActiveDialog(chatIdUser)) {   // является ли chat id участником активной беседы?
//                    if (!connectService.isPerson(chatIdUser)) {           // chat id - это волонтер?
//                        String checkedMessage = update.getMessage().getText();
//                        checkVolunteerOperation(checkedMessage, chatIdUser);
//                        return;
//                    }
//
//                    if (connectService.isPerson(chatIdUser)) {    // если chat id вопрошающий, то шлем сообщение волонтеру
//                        sendSimpleText(connectService.getVolunteerChatIdByPersonChatId(chatIdUser), update.getMessage().getText());
//
//                    } else {                                        // если нет, то наоборот
//                        sendSimpleText(connectService.getPersonChatIdByChatIdVolunteer(chatIdUser), update.getMessage().getText());
//                    }
//
//                } else {
//
//                    // если пользователь впервые
//                    if (!nurseryDBService.contain(chatIdUser)) {
//                        sendSimpleText(update.getMessage().getChatId(), "Здравствуйте, это питомник домашних животных!");
//                    }
//                    checkMessage(message, chatIdUser);       // При любой непонятной команде выводим главное меню чата
//                }
//                // Регистрация волонтера. Тут же id_chat попадает в базу
//                if (update.getMessage().getText().startsWith("Хочу стать волонтером")) {
//                    Volunteer volunteer = new Volunteer();
//                    volunteer.setBusy(false);
//                    volunteer.setVolunteerChatId(chatIdUser);
//                    connectService.addNewVolunteer(volunteer);
//                }
//                if (update.getMessage().getText().startsWith("Я ухожу")) {
//                    connectService.hasLeftVolunteer(chatIdUser);
//                }
//            }
//            // проверяем ответы от кнопок
//        } else if (update.hasCallbackQuery()) {
//            String message = update.getCallbackQuery().getData();
//            Long idChat = update.getCallbackQuery().getMessage().getChatId();
//            checkMessage(message, idChat);
//        }
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

    public void savePhotoToDatabase(InputStream inputStream) throws SQLException {
        try {
            Connection connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/nursery_bd", "holy_worker", "123"); // Подключение к базе данных
            PreparedStatement preparedStatement = connection.prepareStatement("INSERT INTO data_report (foto) VALUES (?)");
            preparedStatement.setBlob(1, inputStream); // Сохраняем фотографию в базу данных
            preparedStatement.executeUpdate();
            preparedStatement.close();
            connection.close();
        } catch (SQLException e) {
            e.printStackTrace();
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

