package ru.shipcollision.api.controllers;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

/**
 * Контроллер проверки доступности API.
 */
@RestController
public class ToolController {

    /**
     * Если этот метод отдает OK - сервис доступен.
     */
    @RequestMapping(path = "/ping", method = RequestMethod.HEAD)
    public void doHead(HttpServletResponse response) {
        response.setHeader("Status", "OK");
    }
}
