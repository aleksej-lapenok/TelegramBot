package ru.ifmo.telegram.bot.services.telegramApi;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by Cawa on 18.11.2017.
 */
@Data
@AllArgsConstructor
public class Update {
    TypeUpdate type;
    String data;
    Integer chatId;
    Integer userId;
    Integer update_id;

    Update(Integer from, TypeUpdate type, String data, Integer update_id) {
        this(type,data, from, from, update_id);
    }

    public enum TypeUpdate {
        MESSAGE,
        CALLBACK_QUEARY
    }
}
