package ru.ifmo.telegram.bot.services.telegramApi;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.ifmo.telegram.bot.services.telegramApi.classes.TypeUpdate;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Update;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Cawa on 18.11.2017.
 */

@Service
public class UpdatesCollector {

    private Logger logger=LoggerFactory.getLogger(this.getClass());

    UpdatesCollector() {}

    public List<Update> getUpdates(String response) throws TgException {
        logger.info(response);
        List<Update> updates = new ArrayList<>();
        JsonParser parser = new JsonParser();
        JsonElement jo = parser.parse(response);
        if (!jo.isJsonObject()) {
            throw new TgException("Bad response");
        }
        logger.info(updates.toString());
        if (!jo.getAsJsonObject().get("ok").getAsBoolean()) {
            throw new TgException("Response code is not ok");
        }
        JsonArray array = jo.getAsJsonObject().get("result").getAsJsonArray();
        array.forEach(
                it -> {
                    logger.info(it.toString());
                    JsonObject message = it.getAsJsonObject();
                    long update_id = message.get("update_id").getAsLong();
                    if (message.get("message") != null) {
                        JsonObject obj = message.get("message").getAsJsonObject();
                        Long messageId = obj.get("message_id").getAsLong();
                        Long from = obj.get("from").getAsJsonObject().get("id").getAsLong();
                        Long where = obj.get("chat").getAsJsonObject().get("id").getAsLong();
                        String text = obj.get("text").getAsString();
                        String username = obj.get("from").getAsJsonObject().get("username") != null ?
                                obj.get("from").getAsJsonObject().get("username").getAsString() :
                                obj.get("from").getAsJsonObject().get("first_name").getAsString();
                        updates.add(new Update(TypeUpdate.MESSAGE, text, username, from, where, update_id, messageId));
                        return;
                    }
                    if (message.get("callback_query") != null) {
                        JsonObject obj = message.get("callback_query").getAsJsonObject();
                        Long from = obj.get("from").getAsJsonObject().get("id").getAsLong();
                        String username = obj.get("from").getAsJsonObject().get("username") != null ?
                                obj.get("from").getAsJsonObject().get("username").getAsString() :
                                obj.get("from").getAsJsonObject().get("first_name").getAsString();
                        Long where = from;
                        Long messageId = 0L;
                        if (obj.get("message") != null) {
                            JsonObject msg = obj.get("message").getAsJsonObject();
                            where = msg.get("chat").getAsJsonObject().get("id").getAsLong();
                            messageId = msg.get("message_id").getAsLong();
                        }
                        String data = obj.get("data").getAsString();
                        updates.add(new Update(TypeUpdate.CALLBACK_QUERY, data, username, from, where, update_id, messageId));
                    }
                }
        );

        return updates;
    }



}
