package com.nursery.nursery_api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Протестировал
 * Локальный сервер поднимается, метод test возвращает текст.
 */
@RestController
public class TestController {

    @GetMapping(path = "test")
    public String test(){
        return "Hi";
    }
}
