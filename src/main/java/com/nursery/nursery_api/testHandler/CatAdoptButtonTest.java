//package com.nursery.nursery_api.testHandler;
//
//import com.nursery.nursery_api.bot.TelegramBot;
//import com.nursery.nursery_api.service.NurseryDBService;
//import org.springframework.stereotype.Component;
//
//@Component
//public class CatAdoptButtonTest extends ButtonHandler{
//
//     String ADOPT_MESSAGE = "Здравствуйте! Здесь Вы можете узнать информацию о том, " +
//            "как забрать кошку.";
//    String[] buttonsNameForCat = {"Правила знакомства с животным до того, как забрать его из приюта.",
//            "Список документов, необходимых для того, чтобы взять животное ",
//            "Список рекомендаций по транспортировке животного",
//            "Список рекомендаций по обустройству дома для котенка",
//            "Список рекомендаций по обустройству дома для взрослой кошки",
//            "Список рекомендаций по обустройству дома для кошки-инвалида",
//            "Список причин, почему могут отказать и не дать забрать кошку из приюта.  ",
//            "Оставить свои контактные данные",
//            "Связаться с волонтером",
//            "К главному меню"};
//
//    String[] callDataCat = {"-knowPet",
//            "-docs", "-transportation",
//            "-baby",
//            "-adult",
//            "-disabled",
//            "-refusal",
//            "-contactCat",
//            "-volunteer",
//            "-main"};
//
//    public CatAdoptButtonTest(TelegramBot bot, NurseryDBService nurseryDBService) {
//        super(bot, nurseryDBService);
//    }
//
//    @Override
//    public void handleCommand(String command) {
//        if (command.equals("-catAdopt")) {
//            sendBotMessageService.sendMessage(idChat.toString(),ADOPT_MESSAGE, buttonsNameForCat, callDataCat);
//        }
//    }
//}
