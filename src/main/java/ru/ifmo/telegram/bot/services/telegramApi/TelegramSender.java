package ru.ifmo.telegram.bot.services.telegramApi;

import org.apache.commons.io.IOUtils;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


@Service
public class TelegramSender {

    public TelegramSender(@Value("${bot-token}") String token) {
        this.token = token;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String token;

    private CloseableHttpResponse sendRequest(String type, List<NameValuePair> args) throws IOException{
        CloseableHttpClient httpclient = HttpClients.createDefault();
        String url = "https://api.telegram.org/bot" + token + "/" + type;
        // logger.info(url);
        HttpPost httpPost = new HttpPost(url);
        httpPost.setEntity(new UrlEncodedFormEntity(args));
        return httpclient.execute(httpPost);
    }

    public String sendMessage(Long id, String text) throws TgException {
        logger.info("Sending: " + text + ", to " + id.toString());
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("chat_id", id.toString()));
            nvps.add(new BasicNameValuePair("text", text));
            CloseableHttpResponse response2 = sendRequest("sendMessage", nvps);
            InputStream tmp = response2.getEntity().getContent();
            // logger.info(result);
            return IOUtils.toString(tmp, "UTF-8");
        } catch (Exception e){
            logger.info(e.getMessage());
            throw new TgException("Error on sendMessage occured.", e);
        }
    }

    public String getUpdates(Long offset) throws TgException{
        logger.info("Getting updates from offset = " + offset.toString());
        try {
            List<NameValuePair> nvps = new ArrayList<>();
            nvps.add(new BasicNameValuePair("offset", offset.toString()));
            CloseableHttpResponse response2 = sendRequest("getUpdates", nvps);
            // logger.info(response2.toString());
            InputStream tmp = response2.getEntity().getContent();
            // logger.info(result);
            return IOUtils.toString(tmp, "UTF-8");
        } catch (Exception e){
            throw new TgException("Error on get Updates occured.", e);
            //Deprecated
            //httpClient.getConnectionManager().shutdown();
        }
    }
}
