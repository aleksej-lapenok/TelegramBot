package ru.ifmo.telegram.bot.services.telegramApi;

import com.fasterxml.jackson.databind.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.HttpURLConnection;
import java.net.URL;
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

    List<Update> getUpdates(Integer number, String response) {
        logger.info(response);
        List<Update> updates = new LinkedList<>();
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode actualObj = mapper.readTree(response);
            //System.out.println(actualObj.get("response").get("items").isArray());
            actualObj = actualObj.get("response");
            for (JsonNode jpost : actualObj) {
                if (jpost.has("message")) {
                    JsonNode obj = jpost.get("message");
                    Integer from = obj.get("from").get("id").asInt();
                    Integer where = obj.get("chat").get("id").asInt();
                    String text = obj.get("text").asText();
                    updates.add(new Update(from, where, "message", text));
                } else {
                    if (jpost.has("callback_query")) {
                        JsonNode obj = jpost.get("callback_query");
                        Integer from = obj.get("from").get("id").asInt();
                        Integer where;
                        if (obj.has("message")) {
                            where = obj.get("message").get("chat").get("id").asInt();
                        } else {
                            where = from;
                        }
                        String data = obj.get("data").asText();
                        updates.add(new Update(from, where, "callback_query", data));
                    } else {
                        logger.info("unsupported update");
                    }
                }
                int i  = (int) (System.currentTimeMillis() / 1000 -  jpost.get("date").asInt()) / 3600;
            }
        } catch (Exception e) {
            System.err.print("hi");
        }
        return updates;
    }
}
