package com.nursery.nursery_api.handler.responseForPerson;

import com.nursery.nursery_api.SomeClasses.PostMessagePerson;
import com.nursery.nursery_api.bot.TelegramBot;
import com.nursery.nursery_api.handler.VolunteerHandler;
import com.nursery.nursery_api.service.ConnectService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Component
public class CallVolunteer implements VolunteerHandler {

    @Override
    public void handle(Long idChat, TelegramBot bot, ConnectService connectService) {
        PostMessagePerson postMessagePerson=new PostMessagePerson(idChat,"Доброго времени суток, у меня вопрос");
        connectService.addQueueMessage(postMessagePerson);

        try {
            bot.execute(
                    SendMessage.
                            builder().
                            chatId(idChat).
                            text("Ваш вопрос отправлен в очередь. В ближайшее время с вам ответят").
                            build()
            );
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean supply(String inputMessage) {
        return inputMessage.equals("-volunteer");
    }
}
