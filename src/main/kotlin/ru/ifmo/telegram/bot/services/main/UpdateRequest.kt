package ru.ifmo.telegram.bot.services.main

import com.sun.jmx.remote.internal.ArrayQueue
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.repository.PlayerRepository
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.telegramApi.TelegramSender
import ru.ifmo.telegram.bot.services.telegramApi.UpdatesCollector
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

@Service
class UpdateRequest(@Value("\${bot-token}") val token: String,
                    val updatesCollector: UpdatesCollector,
                    val playerRepository: PlayerRepository,
                    val mainGameFactory: MainGameFactory) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var lastUpdate = 0L
    private val games = mutableMapOf<Long, Game<*>>()
    private val query = Games.values().toMutableList().map { it.name to ArrayQueue<Long>(5) }.toMap()

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val ts = TelegramSender(token)
        val response = ts.getUpdates(lastUpdate + 1)
        val result = updatesCollector.getUpdates(response)
        if (true) {
            lastUpdate = result.maxBy { it.update_id }?.update_id ?: lastUpdate
            for (update in result) {
                logger.info(update.data)
                if (update.data=="/start") {
                    if (playerRepository.findByChatId(update.chatId) == null) {
                        playerRepository.save(Player(name = update.name!!, chatId = update.chatId))
                    }
                    val player = playerRepository.findByChatId(update.chatId)
                    val text = player!!.name + " lalka"
                    ts.sendMessage(player.chatId, text)
                    ts.getUpdates()
                }
                if (update.data.contentEquals("/game")) {
                    if (games[update.chatId] != null) {
                        logger.info("You should surrender previouse game, before start new")
                        continue
                    }
                    val name = update.data.split(" ")[1]
                    if (!query.containsKey(name)) {
                        logger.info("UnKnown game")
                        continue
                    }
                    val factory = mainGameFactory.getGameFactory(name)
                    if (query[name]?.size!! + 1 >= factory!!.minNumberPlayers()) {
                        val playes = query[name]!!.toMutableList()
                        query[name]!!.clear()
                        playes.add(update.chatId)
                        val game = factory.getGame(*playes.map { playerRepository.findByChatId(it)!! }.toTypedArray())
                        playes.forEach {
                            games.put(it, game)
                            logger.info("start game for $it")
                        }
                    } else {
                        query[name]!!.add(update.chatId)
                    }
                }
                if (update.data.contentEquals("/surrender")) {
                    if (games[update.chatId] == null) {
                        logger.info("You should start game")
                        continue
                    }

                }
            }
        } else
            logger.warn(response)
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