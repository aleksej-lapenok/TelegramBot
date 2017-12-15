package ru.ifmo.telegram.bot.services.main

import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.repository.PlayerRepository
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.game.Step
import ru.ifmo.telegram.bot.services.telegramApi.TelegramSender
import ru.ifmo.telegram.bot.services.telegramApi.TypeUpdate
import ru.ifmo.telegram.bot.services.telegramApi.Update
import ru.ifmo.telegram.bot.services.telegramApi.UpdatesCollector
import ru.ifmo.telegram.bot.services.telegramApi.classes.Button
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard
import java.io.File

@Service
class UpdateRequest(
        val updatesCollector: UpdatesCollector,
        val playerRepository: PlayerRepository,
        val mainGameFactory: MainGameFactory,
        val telegramSender: TelegramSender) {

    private val logger = LoggerFactory.getLogger(this.javaClass)
    private var lastUpdate = 0L
    private val games = mutableMapOf<Player, Game<*>>()
    private val query = Games.values().toMutableList().map { it.name to mutableSetOf<Player>() }.toMap()
    //    private val invents = mutableMapOf<Player, MutableList<Invent>>()
    private val privateGames = mutableMapOf<Player, PrivateGame>()

    @Scheduled(fixedDelay = 1000)
    fun getUpdates() {
        val response = telegramSender.getUpdates(lastUpdate + 1)
        val result = updatesCollector.getUpdates(response)
        lastUpdate = result.maxBy { it.update_id }?.update_id ?: lastUpdate
        for (update in result) {
            val player = getOrCreatePlayer(update)
            // sendFileToPlayer(player, File("pic.png"))
            logger.info(update.data)
            if (update.data.startsWith("/skip")) {
                continue
            }
            if (update.data.startsWith("/game")) {
                var game = getGameByPlayer(player)
                if (game != null) {
                    sendToPlayer(player, "You should finish previous game, before start new")
                    continue
                }
                val name = update.data.split(" ")[1].toUpperCase()

                try {
                    Games.valueOf(name)
                } catch (e: IllegalArgumentException) {
                    sendToPlayer(player, "Unknown game")
                    continue
                }

                addPlayerInQuery(player, name)
                game = tryToGetNewGame(name)
                if (game == null) {
                    sendToPlayer(player, "waiting other players")
                } else {
                    startGame(game)
                }
                continue
            }
            if (update.data.startsWith("/surrender")) {
                if (update.type == TypeUpdate.CALLBACK_QUERY) {
                    telegramSender.hideKeyboard(update)
                }
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
                removePlayerFromGame(player)
                continue
            }
            if (update.data.startsWith("/turn")) {
                if (update.type == TypeUpdate.CALLBACK_QUERY) {
                    telegramSender.hideKeyboard(update)
                }
                val game = getGameByPlayer(player)
                if (game == null) {
                    sendToPlayer(player, "You should start game")
                    continue
                }
                val stepFactory = mainGameFactory.getStepFactory(game.getGameId())!!
                val step = stepFactory.getStep(update.data.substring(update.data.indexOfFirst { it == ' ' } + 1), player)
                sendToPlayer(player, (game as Game<Step>).step(step).first)
                game.getPlayes()
                        .forEach {
                            if (game.isCurrent(it)) {
                                sendToPlayer(it, game.getMessage(it), game.getKeyboard(it))
                            } else {
                                sendToPlayer(it, game.getMessage(it))
                            }
                        }
                if (game.isFinished()) {
                    game.getPlayes().forEach {
                        sendToPlayer(it, "game finished")
                        removePlayerFromGame(player)
                    }
                }
                continue
            }
            if (update.data.startsWith("/create")) {
                val game = getGameByPlayer(player)
                if (game != null) {
                    sendToPlayer(player, "You should finish game, before create own game")
                    continue
                }
                val privateGame = getPrivateGameByPlayer(player)
                if (privateGame != null) {
                    sendToPlayer(player, "You should play in ${privateGame.game.name} or delete it with command /delete or leave with command /leave")
                    continue
                }
                val name = update.data.split(" ")[1]
                try {
                    Games.valueOf(name)
                } catch (e: IllegalArgumentException) {
                    sendToPlayer(player, "Unknown game")
                    continue
                }
                createPrivateGame(player, name)
                sendToPlayer(player, "Game created, use /invent <username> to invent your friends")
                val factory = mainGameFactory.getGameFactory(name)!!
                sendToPlayer(player, "Your can send ${factory.maxNumberPlayers() - 1} inventions")
                sendToPlayer(player, "To start game use command /startGame")
                continue
            }
            if (update.data.startsWith("/delete")) {
                val privateGame = getPrivateGameByPlayer(player)
                if (privateGame == null) {
                    sendToPlayer(player, "You can't delete game, because you didn't create it")
                    continue
                }
                privateGame.players.forEach { removePlayerFromPrivateGame(player) }
                privateGame.players.forEach { sendToPlayer(it, "${player.name} deleted this game") }
                privateGame.inventions.forEach { sendToPlayer(it, "${player.name} deleted game ${privateGame.game.name}, invention not valid") }

                continue
            }
            if (update.data.startsWith("/leave")) {
                val privateGame = getPrivateGameByPlayer(player)
                if (privateGame == null) {
                    sendToPlayer(player, "You already out of privateGame")
                    continue
                }
                assert(privateGame.players.contains(player))
                removePlayerFromPrivateGame(player)
                privateGame.players.remove(player)
                privateGame.players.forEach { sendToPlayer(it, "${player.name} left game") }
                privateGame.inventions.forEach { sendToPlayer(it, "${player.name} left game") }
                sendToPlayer(player, "You left game")
                continue
            }
            if (update.data.startsWith("/invent")) {

                val privateGame = getPrivateGameByPlayer(player)
                if (privateGame == null) {
                    sendToPlayer(player, "not found your game")
                    continue
                }
                if (privateGame.creator != player) {
                    sendToPlayer(player, "Your can't invite anyone")
                    continue
                }
                val factory = mainGameFactory.getGameFactory(privateGame.game)!!
                if (privateGame.players.size + privateGame.inventions.size + 1 > factory.maxNumberPlayers()) {
                    sendToPlayer(player, "No places in game")
                    continue
                }
                val name = update.data.split(" ")[1]
                val player2 = playerRepository.findByName(name)
                if (player2 == null) {
                    sendToPlayer(player, "Unknown player $player2")
                    continue
                }
                if (privateGame.inventions.contains(player2)) {
                    sendToPlayer(player, "You already invented ${player2.name}")
                    continue
                }
                if (privateGame.players.contains(player2)) {
                    sendToPlayer(player, "${player2.name} already in game")
                    continue
                }
                val keyBoard = Keyboard()
                keyBoard.addButton(Button("callback_data", "/accept ${player.chatId}", "Accept"))
                keyBoard.addButton(Button("callback_data", "/hide ${player.chatId}", "Not accept"))
                sendToPlayer(player2, "You reserve invention into ${privateGame.game.name} from ${player.name}", keyBoard)
                sendToPlayer(player, "Inventions was sent")
                privateGame.inventions.add(player2)
                continue
            }
            if (update.data.startsWith("/hide")) {
                val id = update.data.split(" ")[1].toLong()
                if (update.type == TypeUpdate.CALLBACK_QUERY) {
                    telegramSender.hideKeyboard(update)
                }
                val player2 = playerRepository.findByChatId(id)
                if (player2 == null) {
                    sendToPlayer(player, "Strange command")
                    continue
                }
                val game = getPrivateGameByPlayer(player2)
                if (game == null || !game.inventions.contains(player)) {
                    sendToPlayer(player, "No invention")
                    continue
                }
                game.inventions.remove(player)
                game.players.forEach { sendToPlayer(it, "${player.name} didn't accept invention") }
                game.inventions.forEach { sendToPlayer(it, "${player.name} didn't accept invention") }
                sendToPlayer(player, "You refused invention")
                continue
            }
            if (update.data.startsWith("/accept")) {
                if (getGameByPlayer(player) != null || getPrivateGameByPlayer(player) != null) {
                    sendToPlayer(player, "You can't accept it, because you accepted other invention or you're in game")
                    continue
                }
                val id = update.data.split(" ")[1].toLong()
                val player2 = playerRepository.findByChatId(id)
                if (player2 == null) {
                    sendToPlayer(player, "Strange command")
                    continue
                }
                if (update.type == TypeUpdate.CALLBACK_QUERY) {
                    telegramSender.hideKeyboard(update)
                }
                val game = getPrivateGameByPlayer(player2)
                if (game == null || !game.inventions.contains(player)) {
                    sendToPlayer(player, "No invention")
                    continue
                }
                game.players.add(player)
                game.inventions.remove(player)

                game.players.forEach { sendToPlayer(it, "${player.name} in game") }
                game.inventions.forEach { sendToPlayer(it, "${player.name} in game") }
                addPlayerInPrivateGame(player, game)
                continue
            }
            if (update.data.startsWith("/startGame")) {
                val game = tryToGetPrivateGame(player)
                if (game == null) {
                    sendToPlayer(player, "You can't start game")
                    continue
                }
                game.getPlayes().forEach { removePlayerFromPrivateGame(it) }
                startGame(game)
                continue
            }
            if (update.data.startsWith("/start")) {
                val text = player.name + " registered"
                sendToPlayer(player, text)
                continue
            }
            if (update.data.startsWith("/help")) {
                sendToPlayer(player, "/game <nameGame> to start game\n" +
                        "/turn <arguments of turn> to make turn \n" +
                        "/surrender to exit from game")
                sendToPlayer(player, "Game names: ${Games.values().map { it.name }}")
                continue
            }
            sendToPlayer(player, "Unknown command: ${update.data}")
        }
    }

    fun <T : Step> startGame(game: Game<T>) {
        game.getPlayes().forEach {
            if (game.isCurrent(it)) {
                sendToPlayer(it, game.getMessage(it), game.getKeyboard(it))
            } else {
                sendToPlayer(it, game.getMessage(it))
            }
        }
    }

    fun sendToPlayer(player: Player, message: String) = telegramSender.sendMessage(player.chatId, message)!!
    fun sendToPlayer(player: Player, message: String, keyboard: Keyboard) = telegramSender.sendMessage(player.chatId, message, keyboard)!!
    fun sendFileToPlayer(player: Player, file: File) = telegramSender.sendPicture(player.chatId, file)!!

    fun addPlayerInGame(player: Player, game: Game<*>) {
        games[player] = game
    }

    fun addPlayerInPrivateGame(player: Player, privateGame: PrivateGame) {
        privateGames[player] = privateGame
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

    fun getGameByPlayer(player: Player) = games[player]

    fun getPrivateGameByPlayer(player: Player) = privateGames[player]

    fun createPrivateGame(player: Player, game: String) = privateGames.put(player, PrivateGame(Games.valueOf(game), player))

    fun tryToGetPrivateGame(player: Player): Game<*>? {
        val privateGame = getPrivateGameByPlayer(player) ?: return null
        val factory = mainGameFactory.getGameFactory(privateGame.game)!!
        return if (privateGame.players.size >= factory.minNumberPlayers() && privateGame.creator == player) {
            val game = factory.getGame(*privateGame.players.toTypedArray())
            privateGame.players.forEach { addPlayerInGame(it, game) }
            game
        } else {
            null
        }
    }

    fun removePlayerFromGame(player: Player) = games.remove(player)

    fun removePlayerFromPrivateGame(player: Player) {
        privateGames.remove(player)
    }

    fun addPlayerInQuery(player: Player, games: String) = query[games]!!.add(player)

    data class Invent(val game: PrivateGame, val playerTo: Player)

    data class PrivateGame(val game: Games, val creator: Player) {

        val players = mutableSetOf(creator)
        val inventions = mutableSetOf<Player>()
    }
}