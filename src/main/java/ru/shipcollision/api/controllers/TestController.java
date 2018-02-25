package ru.shipcollision.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

/**
 * Контроллер проверки доступности API.
 */
@RestController
@RequestMapping(path = "/test")
public class TestController {

    /**
     * Если этот метод отдает 200 OK, сервис доступен.
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public void doHead() {

    }
}
