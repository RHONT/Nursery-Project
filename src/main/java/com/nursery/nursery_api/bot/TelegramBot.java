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
import org.telegram.telegrambots.meta.api.methods.commands.SetMyCommands;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.commands.scope.BotCommandScopeDefault;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static com.nursery.nursery_api.bot.StandartBotCommand.sendOnlyText;

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
    private final List<DataReportHandler> dataReportHandlers;
    private final List<RegisterHandler> registerHandlers;

    private final SendBotMessageService sendBotMessageService = new SendBotMessageServiceImpl(this);
    private final ConnectService connectService;
    private final ReportService reportService;

    private final VolunteerService volunteerService;

    List<BotCommand> botCommands = new ArrayList<>(List.of(
            new BotCommand("/main", "Выбор питомника"),
            new BotCommand("/main_volunteer", "Операции волонтера")));

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

    @PostConstruct
    private void init() {
        try {
            this.execute(new SetMyCommands(botCommands, new BotCommandScopeDefault(), null));
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpdateReceived(Update update) {
        if (update.hasMessage() && update.getMessage().hasPhoto()) {

            PhotoProcessing(update);

        } else if (update.hasMessage() && update.getMessage().hasText() &&
                !update.getMessage().hasPhoto() && !update.getMessage().getText().isEmpty()) {

            сommandProcessing(update.getMessage().getChatId(), update);

        } else if (update.hasCallbackQuery()) {

            callBackQueryProcessing(update);
        }
    }

    private void callBackQueryProcessing(Update update) {
        String message = update.getCallbackQuery().getData();
        Long idChat = update.getCallbackQuery().getMessage().getChatId();

        if (runDataReportHandlers(message, idChat, update)) {
            return;
        }
        if (runReportHandlers(message, update.getCallbackQuery().getMessage())) {
            return;
        }
        if (runVolunteerHandlers(message, idChat)) {
            return;
        }

        if (runNurseryHandlers(message, idChat)) {
            return;
        }

        if (runVolunteerCommandHandlers(message, idChat)) {
            return;
        }
    }

    private void PhotoProcessing(Update update) {
        Message message = update.getMessage();
        Chat chat = message.getChat();
        // проверяем отчет, если есть фото, значит это отчет
        if (reportService.containPersonForReport(chat.getId())) {
            saveToDB(chat, message, update, nurseryDBService.getVisitors().get(chat.getId()));
            reportService.deletePersonForReport(chat.getId());   // удаляем из списка
        } else {
            sendOnlyText(chat.getId(), "Фото можно присылать только если вы выбрали в меню - 'Отправить отчет'");
        }
    }

    private void сommandProcessing(long chatIdUser, Update update) {
        String badCommand = "Команда не распознана. \nВыберете в меню - выбор питомника\nесли вы являетесь волонтером - меню волонтеров";

        if (checkConnection(chatIdUser, update)) {
            return;
        }

        if (checkEnterMainMenu(chatIdUser, update)) {
            return;
        }

        if (checkFastOperationVolunteer(chatIdUser, update)) {
            return;
        }

        if (checkIsVolunteer(chatIdUser, update)) {
            return;
        }

        if (checkRegistration(update.getMessage().getChatId(), update.getMessage().getText())) {
            return;
        }

        sendOnlyText(update.getMessage().getChatId(), badCommand);
    }

    /**
     * @param message - строка берется из CallbackQuery. Это значение, что лежит "под кнопкой"
     * @param chatId
     */
    private boolean runNurseryHandlers(String message, Long chatId) {
        for (var element : nurseryHandlerList) {
            if (element.supply(message)) {
                element.handle(chatId, this, nurseryDBService, sendBotMessageService);
                return true;
            }
        }
        return false;
    }

    private boolean runVolunteerHandlers(String message, Long chatId) {
        for (var element : volunteerHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this, connectService);
                return true;
            }
        }
        return false;
    }

    private boolean runReportHandlers(String callBackString, Message message) {
        for (var element : reportHandlers) {
            if (element.supply(callBackString)) {
                element.handle(message, this, reportService, nurseryDBService, sendBotMessageService, connectService);
                return true;
            }
        }
        return false;
    }

    private boolean runDataReportHandlers(String message, Long chatId, Update update) {
        for (var element : dataReportHandlers) {
            if (element.supply(message)) {
                element.handle(chatId, this, update, reportService, sendBotMessageService);
                return true;
            }
        }
        return false;
    }

    /**
     * @param message         - строк приходит от волонтера
     * @param chatIdVolunteer Проверяем является ли эта строка командой.
     */
    private boolean runVolunteerCommandHandlers(String message, Long chatIdVolunteer) {
        for (var element : volunteerCommandHandlers) {
            if (element.supply(message)) {
                element.handle(chatIdVolunteer, this, connectService);
              return true;
            }
        }
        return false;
    }

    /**
     * сохраняем фото и сообщение из caption отдельно в БД
     *
     * @param chat
     * @param message
     * @param update
     */
    private void saveToDB(Chat chat, Message message, Update update, String nameNursery) {
        String negativeReport = "Дорогой усыновитель, мы заметили, что ты заполняешь отчет не так " +
                "подробно, как необходимо. Пожалуйста, подойди ответственнее к " +
                "этому занятию. В противном случае волонтеры приюта будут обязаны " +
                "самолично проверять условия содержания животного";


        Optional<DataReport> dataReport = dataReportRepository.findDataReportByIdChatAndDateNow(chat.getId(), LocalDate.now(), nameNursery);
        if (dataReport.isPresent()) {
            DataReport dataReport1 = dataReport.get();

            List<PhotoSize> photos = message.getPhoto();
            PhotoSize photo = photos.get(photos.size() - 1);

            String messageCaption = update.getMessage().getCaption();
            if (messageCaption == null) {
                sendOnlyText(chat.getId(), negativeReport);
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
            sendOnlyText(chat.getId(), "Ваш отчет отправлен!");
        } else {
            sendOnlyText(chat.getId(), "Вы не надены в базе " + nameNursery + ". Выберите в меню другой питомник. Или свяжитесь с волонтером");
        }
    }

    private boolean checkIsVolunteer(long chatIdUser, Update update) {
        String message = update.getMessage().getText();
        if (message.equals("/main_volunteer")) {
            if (volunteerService.isVolunteer(chatIdUser)) {
                runReportHandlers(message, update.getMessage());
            } else
                sendOnlyText(chatIdUser, "Вы не являетесь волонтером. Если есть желание помочь, свяжитесь с нашими консультантами");
            return true;
        } else return false;
    }

    private boolean checkFastOperationVolunteer(long chatIdUser, Update update) {
        String message = update.getMessage().getText();

        // Регистрация волонтера. Тут же id_chat попадает в базу
        if (message.startsWith("Хочу стать волонтером")) {
            Volunteer volunteer = new Volunteer();
            volunteer.setBusy(false);
            volunteer.setVolunteerChatId(chatIdUser);
            connectService.addNewVolunteer(volunteer);
            return true;
        }
        if (message.startsWith("Я ухожу")) {
            connectService.iAmGonnaWayVolunteer(chatIdUser);
            return true;
        }
        return false;
    }

    private boolean checkEnterMainMenu(long chatIdUser, Update update) {
        String maybeMain = update.getMessage().getText();
        // если пользователь впервые
        if (maybeMain.equals("/main")) {
            if (!nurseryDBService.contain(chatIdUser)) {
                sendOnlyText(update.getMessage().getChatId(), "Здравствуйте, это питомник домашних животных!");
            }
            runNurseryHandlers(update.getMessage().getText(), chatIdUser);
            return true;
        }
        return false;
    }

    private boolean checkConnection(long chatIdUser, Update update) {
        if (connectService.containInActiveDialog(chatIdUser)) {   // является ли chat id участником активной беседы?
            if (!connectService.isPerson(chatIdUser)) {           // chat id - это волонтер?
                if (Objects.equals(update.getMessage().getText(), "Конец")) {
                    runVolunteerCommandHandlers("Конец", chatIdUser);
                    return true;
                }
            }

            if (connectService.isPerson(chatIdUser)) {    // если chat id вопрошающий, то шлем сообщение волонтеру
                sendOnlyText(connectService.getVolunteerChatIdByPersonChatId(chatIdUser), update.getMessage().getText());

            } else {                                        // если нет, то наоборот
                sendOnlyText(connectService.getPersonChatIdByChatIdVolunteer(chatIdUser), update.getMessage().getText());
            }
            return true;

        }
        return false;
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
}






