package ru.ifmo.telegram.bot.services.main

import com.google.gson.JsonParser
import com.sun.jmx.remote.internal.ArrayQueue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.repository.PlayerRepository
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.telegramApi.UpdatesCollector
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL
import java.nio.charset.Charset

@Service
class UpdateRequest(@Value("\${bot-token}") val token: String,
                    val updatesCollector: UpdatesCollector,
                    val playerRepository: PlayerRepository,
                    val mainGameFactory: MainGameFactory) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var lastUpdate = 0L
    private val games = mutableMapOf<Long, Game<*>>()
    private val query = Games.values().toMutableList().map { it.name to mutableSetOf<Long>() }.toMap()

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val baseUrl = "https://api.telegram.org/bot$token/"
        val parser = JsonParser()

        val response = parser.parse(URL("${baseUrl}getupdates?offset=${lastUpdate + 1}").readText(Charset.defaultCharset()))
                .takeIf { it.isJsonObject }?.asJsonObject ?: throw Exception()
        val ok = response["ok"]?.takeIf { it.isJsonPrimitive }?.asBoolean ?: throw Exception()
        if (ok) {
            val result = updatesCollector.getUpdates(response["result"]?.asJsonArray)
            lastUpdate = result.maxBy { it.update_id }?.update_id ?: lastUpdate
            for (update in result) {
                if (update.data == "/start") {
                    if (playerRepository.findByChatId(update.chatId) == null) {
                        playerRepository.save(Player(name = update.name!!, chatId = update.chatId))
                    }
                }
                if (update.data.startsWith("/game")) {
                    if (games[update.chatId] != null) {
                        logger.info("You should finish previouse game, before start new")
                        sendMessage(update.chatId, "unfinished")
                        continue
                    }
                    val name = update.data.split(" ")[1]
                    if (!query.containsKey(name)) {
                        logger.info("UnKnown game")
                        sendMessage(update.chatId, "unknown")
                        continue
                    }
                    query[name]!!.add(update.chatId)
                    val factory = mainGameFactory.getGameFactory(name)
                    if (query[name]?.size!! >= factory!!.minNumberPlayers()) {
                        val playes = query[name]!!.toMutableList()
                        query[name]!!.clear()
                        playes.add(update.chatId)
                        val game = factory.getGame(*playes.map { playerRepository.findByChatId(it)!! }.toTypedArray())
                        playes.forEach {
                            games.put(it, game)
                            query.values.forEach { it1 -> it1.remove(it) }
                            sendMessage(it, "started")
                            logger.info("start game for $it")
                        }
                    } else {
                        sendMessage(update.chatId, "waiting")
                    }
                    continue
                }
                if (update.data.startsWith("/finish")) {
                    if (games[update.chatId] == null) {
                        logger.info("You should start game")
                        sendMessage(update.chatId, "no_game")
                        continue
                    }
                    games[update.chatId]!!.finish(playerRepository.findByChatId(update.chatId)!!)
                    sendMessage(update.chatId, "finished")
                    games.remove(update.chatId)

                }
            }
        } else
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

    fun sendMessage(to: Long, text: String) {
        val baseUrl = "https://api.telegram.org/bot$token/"
        URL("${baseUrl}sendMessage?chat_id=${to}&text=${text}").readBytes()
    }

}