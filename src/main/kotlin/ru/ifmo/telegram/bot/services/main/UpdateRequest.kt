package ru.ifmo.telegram.bot.services.main

import com.google.gson.JsonParser
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset
import javax.net.ssl.HttpsURLConnection

@Service
class UpdateRequest(@Value("\${bot-token}") val token: String) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val parser = JsonParser()

        val response = parser.parse(URL("https://api.telegram.org/bot$token/getupdates").readText(Charset.defaultCharset()))
                .takeIf { it.isJsonObject }?.asJsonObject ?: throw Exception()
        val ok = response["ok"]?.takeIf { it.isJsonPrimitive }?.asBoolean ?: throw Exception()
        if (ok)
            logger.info(response["result"]?.asJsonArray.toString())
        //todo: call Sasha's code
        else
            logger.warn(response.asString)
    }

    fun sendPostHttpRequest(url: String, data: String): String {
        with(URL(url).openConnection() as HttpURLConnection) {
            requestMethod = "POST"

            BufferedWriter(OutputStreamWriter(outputStream)).use {
                it.write(data)
            }

            logger.info("Response code: $responseCode")

            BufferedReader(InputStreamReader(inputStream)).use {
                val response = StringBuffer()
                var line: String? = ""
                while (line != null) {
                    response.append(line)
                    line = it.readLine()
                }
                return response.toString()
            }
        }
    }

}