package ru.ifmo.telegram.bot.services.telegramApi;

import com.fasterxml.jackson.databind.*;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Cawa on 18.11.2017.
 */

@Service
public class UpdatesCollector {

    private Logger logger=LoggerFactory.getLogger(this.getClass());

    UpdatesCollector() {

    }

    public List<Update> getUpdates(JsonArray response) {
        logger.info(response.toString());
        List<Update> updates = new ArrayList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            //System.out.println(actualObj.get("response").get("items").isArray());
//            actualObj = actualObj.get("response");
            response.forEach(
                    it -> {
                        JsonObject message = it.getAsJsonObject();
                        long update_id = message.get("update_id").getAsLong();
                        if (message.get("message") != null) {
                            JsonObject obj = message.get("message").getAsJsonObject();
                            Long from = obj.get("from").getAsJsonObject().get("id").getAsLong();
                            Long where = obj.get("chat").getAsJsonObject().get("id").getAsLong();
                            String text = obj.get("text").getAsString();
                            String username = obj.get("from").getAsJsonObject().get("username") != null ?
                                    obj.get("from").getAsJsonObject().get("username").getAsString() :
                                    obj.get("from").getAsJsonObject().get("first_name").getAsString();
                            updates.add(new Update(TypeUpdate.MESSAGE, text, username, from, where, update_id));
                            return;
                        }
                        if (message.get("callback_query") != null) {
                            JsonObject obj = message.get("callback_query").getAsJsonObject();
                            Long from = obj.get("from").getAsJsonObject().get("id").getAsLong();
                            Long where = obj.get("message") != null ?
                                    obj.get("message").getAsJsonObject().get("chat").getAsJsonObject().get("id").getAsLong() :
                                    from;
                            String data = obj.get("data").getAsString();
                            updates.add(new Update(TypeUpdate.CALLBACK_QUEARY, data, null, from, where, update_id));
                        }
                    }
            );
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        return updates;
    }

}
