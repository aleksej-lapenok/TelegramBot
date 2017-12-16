package ru.ifmo.telegram.bot.services.game.blingame

import ru.ifmo.services.game.GameUpdate
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.game.blingame.logic.Board
import ru.ifmo.telegram.bot.services.game.blingame.logic.MyBoard
import ru.ifmo.telegram.bot.services.game.blingame.logic.SBGame
import ru.ifmo.telegram.bot.services.main.Games
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard
import java.io.File
import java.util.regex.Pattern

class SeaBattleGame(player1: Player, player2: Player) : Game<SeaBattleStep> {
    val players = listOf<Player>(player1, player2)
    val game = SBGame()
    val kboard = Keyboard()

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
                            is MyBoard.Companion.ShipPlaceResult.OutOfBounds -> "Ti pidor: out of bounds"
                            is MyBoard.Companion.ShipPlaceResult.NoShipsOfSuchSize -> "Ti pidor: no ships of such size"
                            is MyBoard.Companion.ShipPlaceResult.Intersects -> "Ti pidor: ships intersect"
                            is MyBoard.Companion.ShipPlaceResult.ShipPlaceOK -> "Ti ne pidor"
                        }
                    } else {
                        "Ti pidor: cant parse your shit"
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
                        return Pair("Ti ne pidor", true)
                    } else {
                        return Pair("Ti pidor: out of bounds", false)
                    }
                } else {
                    return Pair("Ti pidor: cant parse your shit", false)
                }
            } else {
                return Pair("Ti pidor: not your turn", false)
            }
            is SBGame.Companion.GameState.GameEnded ->
                return Pair("Ti pidor: game won by ${players[game.whoIsWinner()].name}", false)
        }
    }

    private fun parsePlacingShips(step: SeaBattleStep) : placeShipArgs? {
        val args = step.command.split(Pattern.compile("\n+"));
        if (args.size < 4) {
            return null
        }
        var x = 0
        var y = 0
        var size = 0
        try {
            x = args[0].toInt()
            y = args[1].toInt()
            size = args[2].toInt()
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
        var x = 0
        var y = 0
        try {
            x = args[0].toInt()
            y = args[1].toInt()
        } catch (e : Exception) {
            return null
        }
        return Pair(x, y)
    }

    override fun drawPicture(player: Player): File? = null

    override fun getGameUpdate(player: Player): GameUpdate {
        val state = game.getGameState()
        val (my, enemy) = game.getBoards(playerToId(player))
        val playerId = playerToId(player)
        val msg = when(state) {
            is SBGame.Companion.GameState.PlacingShips -> run {
                val remainingStr = getRemainingSHipsString(game.getRemainingFleet(playerId))
                "Your board:\n${getBoardString(my)}Place your ships\nShips remainig:\n${remainingStr}"
            }
            is SBGame.Companion.GameState.PlayerTurn -> run {
                val bstr = getBoardsString(player)
                "Make your move"
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
        sb.append("pssst... Ti pidor :з\n")
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
        TODO()
    }

    override fun getPlayes(): List<Player> {
        return players
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
            is SBGame.Companion.GameState.PlayerTurn -> players[s.playerId] == player
            is SBGame.Companion.GameState.GameEnded -> false
        }
    }

    fun playerToId(p: Player): Int {
        return if (players[0] == p) 0 else 1
    }

    data class placeShipArgs(val x: Int, val y: Int, val size: Int, val dir: MyBoard.Companion.ShipPlaceDirection)
}