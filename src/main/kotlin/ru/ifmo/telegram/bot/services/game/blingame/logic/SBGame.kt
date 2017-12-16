package ru.ifmo.telegram.bot.services.game.blingame.logic

import com.google.gson.JsonElement
import com.google.gson.JsonObject

class SBGame() {
    private var myBoard1: MyBoard = MyBoard()
    private var myBoard2: MyBoard = MyBoard()
    private var enemyBoard1: EnemyBoard = EnemyBoard()
    private var enemyBoard2: EnemyBoard = EnemyBoard()
//    private val myBoards = listOf<MyBoard>(myBoard1, myBoard2)
    private var myBoards : List<MyBoard>
    private var enemyBoards : List<EnemyBoard>
    private var state: GameState = GameState.PlacingShips

    constructor(gameJson : JsonObject) : this() {
        state = stateFromJson(gameJson.get("state").asJsonObject)
        myBoard1 = MyBoard(gameJson.get("myBoard1").asJsonObject)
        myBoard2 = MyBoard(gameJson.get("myBoard2").asJsonObject)
        enemyBoard1 = EnemyBoard(gameJson.get("enemyBoard1").asJsonObject)
        enemyBoard2 = EnemyBoard(gameJson.get("enemyBoard2").asJsonObject)
    }

    fun fromJson(gameJson : JsonObject) {
        state = stateFromJson(gameJson.get("state").asJsonObject)
        myBoard1 = MyBoard(gameJson.get("myBoard1").asJsonObject)
        myBoard2 = MyBoard(gameJson.get("myBoard2").asJsonObject)
        enemyBoard1 = EnemyBoard(gameJson.get("enemyBoard1").asJsonObject)
        enemyBoard2 = EnemyBoard(gameJson.get("enemyBoard2").asJsonObject)

        myBoards = listOf<MyBoard>(myBoard1, myBoard2)
        enemyBoards = listOf<EnemyBoard>(enemyBoard1, enemyBoard2)
    }

    init {
        myBoards = listOf<MyBoard>(myBoard1, myBoard2)
        enemyBoards = listOf<EnemyBoard>(enemyBoard1, enemyBoard2)
    }

    fun getGameState(): GameState = state

    fun placeShip(playerId: Int,
                  x: Int,
                  y: Int,
                  size: Int,
                  direction: MyBoard.Companion.ShipPlaceDirection)
            : MyBoard.Companion.ShipPlaceResult {
        assert(state == GameState.PlacingShips)
        val res = myBoards[playerId].placeShip(size, x, y, direction)
        if (myBoards.all { it.isAllShipsPlaced() }) {
            state = GameState.PlayerTurn(0)
        }
        return res;
    }

    fun makeMove(playerId: Int, x: Int, y: Int) {
        assert(state == GameState.PlayerTurn(playerId))
        val res = myBoards[nextPlayerId(playerId)].makeMove(x, y)
        enemyBoards[playerId].markTile(x, y, res)
        if (myBoards[nextPlayerId(playerId)].isAllShipsWrecked()) {
            state = GameState.GameEnded(playerId)
        } else if (res == Board.Companion.MoveResult.Miss) {
            state = GameState.PlayerTurn(nextPlayerId(playerId))
        }
    }

    fun getBoards(playerId: Int): Pair<Board, Board> {
        return Pair(myBoards[playerId], enemyBoards[playerId])
    }

    fun whoIsWinner(): Int {
        val s = state
        return if (s is GameState.GameEnded) {
            s.winnerId
        } else {
            -1
        }
    }

    fun surrender(id: Int) {
        state = GameState.GameEnded(nextPlayerId(id))
    }

    fun getRemainingFleet(id: Int): Map<Int, Int> {
        return myBoards[id].getShipsRemainToPlace()
    }

    companion object {
        private fun nextPlayerId(i: Int) = (i + 1) % 2

        private fun stateFromJson(json: JsonObject) : GameState {
            return when(json.get("name").asString) {
                "PlacingShips" -> GameState.PlacingShips
                "PlayerTurn" -> GameState.PlayerTurn(json.get("val").asInt)
                "GameEnded" -> GameState.GameEnded(json.get("val").asInt)
                else -> throw Exception()
            }
        }

        sealed class GameState {
            abstract fun toJson(): JsonObject

            object PlacingShips : GameState() {
                override fun toJson(): JsonObject {
                    val json = JsonObject()
                    json.addProperty("name", "PlacingShips")
                    return json
                }
            }
            data class PlayerTurn(val playerId: Int) : GameState() {
                override fun toJson(): JsonObject {
                    val json = JsonObject()
                    json.addProperty("name", "PlayerTurn")
                    json.addProperty("val", playerId)
                    return json
                }
            }
            data class GameEnded(val winnerId: Int) : GameState() {
                override fun toJson(): JsonObject {
                    val json = JsonObject()
                    json.addProperty("name", "GameEnded")
                    json.addProperty("val", winnerId)
                    return json
                }
            }
        }
    }

    fun toJson(): JsonObject {
        val json = JsonObject()
        json.add("myBoard1", myBoard1.toJson())
        json.add("myBoard2", myBoard2.toJson())
        json.add("enemyBoard1", enemyBoard1.toJson())
        json.add("enemyBoard2", enemyBoard2.toJson())
        json.add("state", state.toJson())
        return json
    }
}