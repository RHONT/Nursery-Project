package com.nursery.nursery_api.bot;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.objects.Update;

import static java.util.Arrays.asList;
import static org.mockito.ArgumentMatchers.any;

@ExtendWith(MockitoExtension.class)
public class TelegramBotTest {

    @Test
    public void OnUpdateReceivedTest() {
        TelegramLongPollingBot bot = Mockito.mock(TelegramLongPollingBot.class);
        Mockito.doCallRealMethod().when(bot).onUpdatesReceived(any());
        Update update1 = new Update();
        update1.setUpdateId(1);
        Update update2 = new Update();
        update2.setUpdateId(2);
        bot.onUpdatesReceived(asList(update1, update2));
        Mockito.verify(bot).onUpdateReceived(update1);
        Mockito.verify(bot).onUpdateReceived(update2);
    }

}
