package ru.ifmo.telegram.bot.services.main

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.repository.PlayerRepository
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.telegramApi.TelegramSender
import ru.ifmo.telegram.bot.services.telegramApi.UpdatesCollector

@Service
class UpdateRequest(
        val updatesCollector: UpdatesCollector,
        val playerRepository: PlayerRepository,
        val mainGameFactory: MainGameFactory,
        val telegramSender: TelegramSender) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var lastUpdate = 0L
    private val games = mutableMapOf<Long, Game<*>>()
    private val query = Games.values().toMutableList().map { it.name to mutableSetOf<Long>() }.toMap()

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val response = telegramSender.getUpdates(lastUpdate + 1)
        val result = updatesCollector.getUpdates(response)
        lastUpdate = result.maxBy { it.update_id }?.update_id ?: lastUpdate
        for (update in result) {
            logger.info(update.data)
            if (update.data.startsWith("/start")) {
                if (playerRepository.findByChatId(update.chatId) == null) {
                    playerRepository.save(Player(name = update.name!!, chatId = update.chatId))
                }
                val player = playerRepository.findByChatId(update.chatId)
                val text = player!!.name + " registered"
                telegramSender.sendMessage(player.chatId, text)
//                    telegramSender.getUpdates()
            }
            if (update.data.startsWith("/game")) {
                if (games[update.chatId] != null) {
                    telegramSender.sendMessage(update.chatId, "You should finish previouse game, before start new")
                    continue
                }
                val name = update.data.split(" ")[1]
                if (!query.containsKey(name)) {
                    logger.info("UnKnown game")
                    continue
                }
                val factory = mainGameFactory.getGameFactory(name)
                query[name]!!.add(update.chatId)
                if (query[name]?.size!! >= factory!!.minNumberPlayers()) {
                    val playes = query[name]!!.toMutableList()
                    query[name]!!.clear()
                    val game = factory.getGame(*playes.map { playerRepository.findByChatId(it)!! }.toTypedArray())
                    playes.forEach {
                        games.put(it, game)
                        query.values.forEach { it2 -> it2.remove(it) }
                        telegramSender.sendMessage(it, game.getMessage(playerRepository.findByChatId(it)!!))
                    }
                } else {
                    telegramSender.sendMessage(update.chatId, "waiting other playes")
                }
            }
            if (update.data.startsWith("/surrender")) {
                if (games[update.chatId] == null) {
                    logger.info("You should start game")
                    continue
                }
                val game = games[update.chatId]!!
                val player = playerRepository.findByChatId(chatId = update.chatId)!!
                games[update.chatId]!!.surrender(player)
                telegramSender.sendMessage(update.chatId, "You left this game")
                for (p in game.getPlayes()) {
                    telegramSender.sendMessage(p.chatId, games[update.chatId]!!.getMessage(p))
                }
                games.remove(update.chatId)
            }
        }
    }

}