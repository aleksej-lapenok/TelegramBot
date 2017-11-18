package ru.ifmo.telegram.bot.services.telegramApi;

/**
 * Created by Cawa on 18.11.2017.
 */
public class Update {
    String type;
    String data;
    Integer chatId;
    Integer userId;

    Update(Integer from, String type, String data) {
        this.data = data;
        chatId = from;
        userId = from;
        this.type = type;
    }

    Update(Integer from, Integer where, String type, String data) {
        this.data = data;
        chatId = where;
        userId = from;
        this.type = type;
    }

}
