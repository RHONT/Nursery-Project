package com.nursery.nursery_api.handler.reportVolunteerCommand;

import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.ReportHandler;
import com.nursery.nursery_api.model.Volunteer;
import com.nursery.nursery_api.repositiry.VolunteerRepository;
import com.nursery.nursery_api.service.ConnectService;
import com.nursery.nursery_api.service.NurseryDBService;
import com.nursery.nursery_api.service.ReportService;
import com.nursery.nursery_api.service.SendBotMessageService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.Objects;

import static com.nursery.nursery_api.Global.GlobalVariable.volunteersList;

@Component
public class MyModeCommand implements ReportHandler {
    private final VolunteerRepository volunteerRepository;

    public MyModeCommand(VolunteerRepository volunteerRepository) {
        this.volunteerRepository = volunteerRepository;
    }

    @Override
    public void handle(Long idChat, TelegramBot bot, ReportService reportService, NurseryDBService nurseryDBService, SendBotMessageService sendBotMessageService, ConnectService connectService) {

        Volunteer volunteer=volunteersList.keySet().stream().filter(e-> Objects.equals(e.getVolunteerChatId(), idChat)).findFirst().get();

        Long opponent=volunteersList.get(volunteer);

        String response="Статус не определен";
        if (reportService.isReportVolunteer(idChat)) {
            response="Вы проверяющий отчеты";
        } else
        if ( volunteersList.
                keySet().
                stream().
                anyMatch(e-> !e.isBusy() && Objects.equals(e.getVolunteerChatId(), idChat)) ) {
            response="Вы ожидаете входящей консультации";
        }else
        if (volunteersList.
                keySet().
                stream().
                anyMatch(e-> e.isBusy() && Objects.equals(e.getVolunteerChatId(), idChat) && opponent>1L)) {
            response="Вы прямо сейчас консультируете человека";
        } else

        if (volunteersList.
                keySet().
                stream().
                anyMatch((e-> e.isBusy() && Objects.equals(e.getVolunteerChatId(), idChat) && opponent==0L))) {
            response="Вы отдыхаете";
        }

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text(response).
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }
    /**
     * сравнивается входящее сообщение от нажатой кнопки с нужным значением кнопки
     * @param inputMessage
     * @return
     */
    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-myMode");
    }
}
