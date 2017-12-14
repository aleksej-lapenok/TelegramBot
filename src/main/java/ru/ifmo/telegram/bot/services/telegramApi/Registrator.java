package ru.ifmo.telegram.bot.services.telegramApi;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;

@Service
public class Registrator {

    @Value("${bot-token}")
    private String token;

    @PostConstruct
    public int register() {

        // System.out.print(token);
        return 0;
    }
}
