package ru.ifmo.telegram.bot.services.game.blingame

import com.google.gson.JsonObject
import com.google.gson.JsonParser
import ru.ifmo.services.game.GameException
import ru.ifmo.services.game.GameUpdate
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.game.blingame.logic.Board
import ru.ifmo.telegram.bot.services.game.blingame.logic.MyBoard
import ru.ifmo.telegram.bot.services.game.blingame.logic.SBGame
import ru.ifmo.telegram.bot.services.main.Games
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard
import java.util.regex.Pattern

class SeaBattleGame(val player1: Player, val player2: Player) : Game<SeaBattleStep> {
    val playerss = listOf<Player>(player1, player2)
    var game = SBGame()
    val kboard = Keyboard()

    constructor(player1: Player, player2: Player, gameJson: JsonObject) : this(player1, player2) {
        game = SBGame(gameJson)
        game.fromJson(gameJson)
    }

    override fun step(step: SeaBattleStep): Pair<String, Boolean> {
        val state = game.getGameState()
        when (state) {
            is SBGame.Companion.GameState.PlacingShips -> run {
                val msg = run {
                    val args = parsePlacingShips(step)
                    if (args != null) {
                        val (x, y, size, dir) = args
                        val res = game.placeShip(playerToId(step.player), x, y, size, dir)
                        when(res) {
                            is MyBoard.Companion.ShipPlaceResult.OutOfBounds -> "Error: out of bounds"
                            is MyBoard.Companion.ShipPlaceResult.NoShipsOfSuchSize -> "Error: no ships of such size"
                            is MyBoard.Companion.ShipPlaceResult.Intersects -> "Error: ships intersect"
                            is MyBoard.Companion.ShipPlaceResult.ShipPlaceOK -> "All ok"
                        }
                    } else {
                        "Error: cant parse your input"
                    }
                }
                return Pair(msg, false)
            }
            is SBGame.Companion.GameState.PlayerTurn -> if (state.playerId == playerToId(step.player)) {
                val args = parseAttack(step)
                if (args != null) {
                    val (x, y) = args
                    if (Board.coordsInBounds(x, y)) {
                        game.makeMove(playerToId(step.player), x, y)
                        return Pair("All ok", true)
                    } else {
                        return Pair("Error: out of bounds", false)
                    }
                } else {
                    return Pair("Error: cant parse your input", false)
                }
            } else {
                return Pair("Error: not your turn", false)
            }
            is SBGame.Companion.GameState.GameEnded ->
                return Pair("Error: game won by ${playerss[game.whoIsWinner()].name}", false)
        }
    }

    private fun parsePlacingShips(step: SeaBattleStep) : placeShipArgs? {
        val args = step.command.split(" ");
        if (args.size < 4) {
            return null
        }
        val x : Int
        val y : Int
        val size : Int
        try {
            y = args[1].toInt()
            size = args[2].toInt()
            if (args[0][0] in 'a' .. 'j') {
                x = args[0][0] - 'a'
            } else {
                throw Exception()
            }
        } catch (e : Exception) {
            return null
        }
        val d = when(args[3]) {
            "r" -> MyBoard.Companion.ShipPlaceDirection.Right
            "l" -> MyBoard.Companion.ShipPlaceDirection.Left
            "u" -> MyBoard.Companion.ShipPlaceDirection.Up
            "d" -> MyBoard.Companion.ShipPlaceDirection.Down
            else -> return null
        }
        return placeShipArgs(x, y, size, d)
    }

    private fun parseAttack(step: SeaBattleStep) : Pair<Int, Int>? {
        val args = step.command.split(" ");
        if (args.size < 2) {
            return null
        }
        val x : Int
        val y : Int
        try {
            y = args[1].toInt()
            if (args[0][0] in 'a' .. 'j') {
                x = args[0][0] - 'a'
            } else {
                throw Exception()
            }
        } catch (e : Exception) {
            return null
        }
        return Pair(x, y)
    }

//    override fun drawPicture(player: Player): File? = null

    override fun getGameUpdate(player: Player): GameUpdate {
        val state = game.getGameState()
        val (my, _) = game.getBoards(playerToId(player))
        val playerId = playerToId(player)
        val msg = when(state) {
            is SBGame.Companion.GameState.PlacingShips -> run {
                val remainingStr = getRemainingSHipsString(game.getRemainingFleet(playerId))
                "Your board:\n${getBoardString(my)}Place your ships\nShips remainig:\n${remainingStr}"
            }
            is SBGame.Companion.GameState.PlayerTurn -> run {
                val bstr = getBoardsString(player)
                "$bstr\nMake your move"
            }
            is SBGame.Companion.GameState.GameEnded -> "Winner name: ${player.name}"
        }
        return GameUpdate(msg, kboard, null)
    }

    private fun getBoardsString(player: Player): String {
        val playerId = playerToId(player)
        val (my, enemy) = game.getBoards(playerId)
        val sb = StringBuilder()
        sb.append("Your board:\n")
        sb.append(getBoardString(my))
        sb.append("Enemy board:\n")
        sb.append(getBoardString(enemy))
        return sb.toString()
    }

    private fun getBoardString(b: Board): String {
        val sb = StringBuilder()
        sb.append("```\n")
        sb.append(b.stringRep())
        sb.append("```\n")
        return sb.toString()
    }

    private fun getRemainingSHipsString(ships: Map<Int, Int>): String {
        val sb = StringBuilder()
        for ((k, v) in ships) {
            sb.append("$k tiles: $v \n")
        }
        return sb.toString()
    }

    override fun surrender(player: Player) {
        game.surrender(playerToId(player))
    }

    override fun toJson(): String {
        return toJsonJson().toString()
    }

    fun toJsonJson(): JsonObject {
        val json = JsonObject()
        json.addProperty("p1", player1.id)
        json.addProperty("p2", player2.id)
        json.add("game", game.toJson())
        return json
    }

    override fun getGameId(): Games {
        return Games.SEABATTLE
    }

    override fun isFinished(): Boolean {
        return game.getGameState() is SBGame.Companion.GameState.GameEnded
    }

    override fun isCurrent(player: Player): Boolean {
        val s = game.getGameState()
        return when(s) {
            is SBGame.Companion.GameState.PlacingShips -> true
            is SBGame.Companion.GameState.PlayerTurn -> playerss[s.playerId] == player
            is SBGame.Companion.GameState.GameEnded -> false
        }
    }

    override fun getPlayers(): List<Player> {
        return playerss
    }

    fun playerToId(p: Player): Int {
        return if (playerss[0] == p) 0 else 1
    }

    data class placeShipArgs(val x: Int, val y: Int, val size: Int, val dir: MyBoard.Companion.ShipPlaceDirection)
}