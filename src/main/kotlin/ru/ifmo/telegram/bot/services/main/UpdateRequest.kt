package ru.ifmo.telegram.bot.services.main

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.repository.PlayerRepository
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.game.Step
import ru.ifmo.telegram.bot.services.telegramApi.TelegramSender
import ru.ifmo.telegram.bot.services.telegramApi.Update
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
    private val query = Games.values().toMutableList().map { it.name to mutableSetOf<Player>() }.toMap()

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val response = telegramSender.getUpdates(lastUpdate + 1)
        val result = updatesCollector.getUpdates(response)
        lastUpdate = result.maxBy { it.update_id }?.update_id ?: lastUpdate
        for (update in result) {
            val player = getOrCreatePlayer(update)
            logger.info(update.data)
            if (update.data.startsWith("/start")) {
                val text = player.name + " registered"
                sendToPlayer(player, text)
                continue
            }
            if (update.data.startsWith("/game")) {

                var game = getGameByPlayer(player)
                if (game != null) {
                    sendToPlayer(player, "You should finish previouse game, before start new")
                    continue
                }
                val name = update.data.split(" ")[1]
                if (!query.containsKey(name)) {
                    logger.info("UnKnown game")
                    continue
                }

                addPlayerInQuery(player, name)
                game = tryToGetNewGame(name)
                if (game == null) {
                    sendToPlayer(player, "waiting other playes")
                } else {
                    game.getPlayes().forEach {
                        sendToPlayer(it, game.getMessage(it))
                    }
                }

                continue
            }
            if (update.data.startsWith("/surrender")) {
                val game = getGameByPlayer(player)
                if (game == null) {
                    sendToPlayer(player, "You should start game before you surrender")
                    continue
                }
                game.surrender(player)
                sendToPlayer(player, "You left this game")
                game.getPlayes().forEach {
                    sendToPlayer(it, game.getMessage(it))
                }
                games.remove(update.chatId)
                continue
            }
            if (update.data.startsWith("/turn")) {
                if (games[update.chatId] == null) {
                    logger.info("You should start game")
                    continue
                }
                val game = games[update.chatId]!!
                val player = playerRepository.findByChatId(update.chatId)!!
                val stepFactory = mainGameFactory.getStepFactory(game.getGameId())!!
                val step = stepFactory.getStep(update.data.substring(update.data.indexOfFirst { it == ' ' } + 1), player)
                telegramSender.sendMessage(update.chatId, (game as Game<Step>).step(step as Step))
                game.getPlayes()
                        .forEach { telegramSender.sendMessage(it.chatId, game.getMessage(it)) }
                continue
            }
            if (update.data.startsWith("/help")) {
                telegramSender.sendMessage(update.chatId, "Use commands: /game <nameGame> to start game\n /turn <arguments of turn> to make turn \n /surrender to exit from game")
                telegramSender.sendMessage(update.chatId, "Game names: ${Games.values().map { it.name }}")
                continue
            }
            telegramSender.sendMessage(update.chatId, "Unknown command: ${update.data}")
        }
    }

    fun sendToPlayer(player: Player, message: String) = telegramSender.sendMessage(player.chatId, message)!!

    fun addPlayerInGame(player: Player, game: Game<*>) {
        games[player.chatId!!] = game
    }

    fun tryToGetNewGame(name: String): Game<*>? {
        val factory = mainGameFactory.getGameFactory(name)
        return if (query[name]?.size!! >= factory!!.minNumberPlayers()) {
            val playes = query[name]!!.toMutableList()
            query[name]!!.clear()
            val game = factory.getGame(*playes.toTypedArray())
            playes.forEach {
                addPlayerInGame(it, game)
            }
            game
        } else {
            null
        }
    }

    fun getOrCreatePlayer(update: Update): Player {
        var player = playerRepository.findByChatId(update.chatId)
        if (player == null) {
            player = Player(name = update.name, chatId = update.chatId)
            player = playerRepository.save(player)
        }
        return player!!
    }

    fun getGameByPlayer(player: Player) = games[player.chatId]

    fun addPlayerInQuery(player: Player, games: String) = query[games]!!.add(player)

}