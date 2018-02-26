package ru.shipcollision.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер проверки доступности API.
 */
@RestController
@RequestMapping(path = "/test")
public class TestController {

    /**
     * Если этот метод отдает OK - сервис доступен.
     */
    @RequestMapping(method = RequestMethod.HEAD)
    public void doHead(HttpServletResponse response) {
        response.setHeader("Status", "OK");
    }
}
