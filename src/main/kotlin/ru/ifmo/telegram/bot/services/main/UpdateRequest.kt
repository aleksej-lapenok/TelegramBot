package ru.ifmo.telegram.bot.services.main

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.ObjectReader
import com.google.gson.JsonParser
import lombok.AllArgsConstructor
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.services.telegramApi.UpdatesCollector
import java.net.URL
import java.nio.charset.Charset
import java.util.*

@Service
class UpdateRequest(val updatesCollector: UpdatesCollector) {

    private val logger = LoggerFactory.getLogger(this.javaClass)

    @Value("\${bot-token}")
    private val token: String? = null

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val parser = JsonParser()

        val response = parser.parse(URL("https://api.telegram.org/bot${token}/getupdates").readText(Charset.defaultCharset()))
                .takeIf { it.isJsonObject }?.asJsonObject ?: throw Exception()
        val ok = response["ok"]?.takeIf { it.isJsonPrimitive }?.asBoolean ?: throw Exception()
        if (ok)
            logger.info(response["result"]?.asJsonArray.toString())
        //todo: call Sasha's code
        else
            logger.warn(response.asString)
    }


}