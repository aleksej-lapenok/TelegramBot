package ru.ifmo.telegram.bot.services.telegramApi;

import org.apache.catalina.util.URLEncoder;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;


@Service
public class TelegramSender {

    public TelegramSender(@Value("${bot-token}") String token) {
        this.token = token;
    }

    private Logger logger = LoggerFactory.getLogger(this.getClass());

    private String token;

    private String encodeAndSendRequest(String type, List<NameValuePair> nvps) throws TgException{
        try {
            return sendRequest(type, new UrlEncodedFormEntity(nvps));
        } catch (UnsupportedEncodingException e) {
            throw new TgException(e);
        }
    }

    private String sendRequest(String type, HttpEntity entity) throws TgException{
        try {
            CloseableHttpClient httpclient = HttpClients.createDefault();
            String url = "https://api.telegram.org/bot" + token + "/" + type;
            HttpPost httpPost = new HttpPost(url);
            httpPost.setEntity(entity);
            CloseableHttpResponse response2 = httpclient.execute(httpPost);
            InputStream tmp = response2.getEntity().getContent();
            // logger.info(IOUtils.toString(tmp, "UTF-8"));
            return IOUtils.toString(tmp, "UTF-8");
        } catch (IOException e) {
            logger.info(e.getMessage());
            throw new TgException("Error on " + type + " occured.", e);
        }
    }

    public String sendMessage(Long id, String text) throws TgException {
        return sendMessage(id, text, new Keyboard());
    }

    public String sendMessage(Long id, String text, Keyboard keyboard) throws TgException {
        logger.info("Sending: " + text + ", to " + id.toString());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("chat_id", id.toString()));
        nvps.add(new BasicNameValuePair("text", text));
        nvps.add(new BasicNameValuePair("reply_markup", keyboard.toJson().toString()));
        return encodeAndSendRequest("sendMessage", nvps);
    }


    public String sendPicture(Long id, File file) throws TgException {
        logger.info("Sending: " + file.getName() + ", to " + id.toString());
        FileBody fileBody = new FileBody(file, ContentType.DEFAULT_BINARY);
        MultipartEntityBuilder builder = MultipartEntityBuilder.create();
        builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
        builder.addPart("photo", fileBody);
        builder.addTextBody("chat_id", id.toString());
        HttpEntity entity = builder.build();
        return sendRequest("sendPhoto", entity);
    }

    public String hideKeyboard(Update update) throws TgException {
        logger.info("hiding keyboard: " + update.getUpdate_id());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("chat_id", Long.toString(update.getChatId())));
        nvps.add(new BasicNameValuePair("message_id", Long.toString(update.getMessage_id())));
        nvps.add(new BasicNameValuePair("reply_markup", new Keyboard().toString()));
        return encodeAndSendRequest("editMessageReplyMarkup", nvps);
    }

    public String getUpdates(Long offset) throws TgException {
        logger.info("Getting updates from offset = " + offset.toString());
        List<NameValuePair> nvps = new ArrayList<>();
        nvps.add(new BasicNameValuePair("offset", offset.toString()));
        return encodeAndSendRequest("getUpdates", nvps);
    }
}
