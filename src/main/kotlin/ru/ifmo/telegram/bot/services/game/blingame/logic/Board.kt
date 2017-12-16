package ru.ifmo.telegram.bot.services.game.blingame.logic

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.ifmo.telegram.bot.services.game.blingame.logic.GamePrefs.BOARD_SIZE

abstract class Board() {
    protected var board = MutableList(GamePrefs.BOARD_SIZE, { MutableList<Tile>(GamePrefs.BOARD_SIZE, { Tile.Empty }) })

    constructor(gameJson : JsonObject) : this() {
        board = mutableListOf()
        val tiles = gameJson.get("tiles").asJsonArray
        board = tiles.map { it.asJsonArray.map { Tile.tileFromJson(it.asJsonObject) }.toMutableList() }.toMutableList()
    }

    fun stringRep(): String {
        val sb = StringBuilder()
        sb.append("  abcdefghij\n")
        sb.append(" +––––––––––\n")
        for (row in 0 until GamePrefs.BOARD_SIZE) {
            sb.append(row);
            sb.append("|");
            for (column in 0 until GamePrefs.BOARD_SIZE) {
                val tile = board[column][row]
                sb.append(when(tile) {
                    is Tile.Empty -> " "
                    is Tile.Missed -> "."
                    is Tile.MyShipTile -> if (tile.isWrecked) "□" else "x"
                    is Tile.EnemyShipTile -> "x"
                })
            }
            sb.append("\n")
        }
        return sb.toString()
    }

    open fun toJson(): JsonObject {
        val array = JsonArray()
        for (list in board) {
            val local = JsonArray()
            for (t in list) {
                local.add(t.toJson())
            }
            array.add(local)
        }
        val k = JsonObject()
        k.add("tiles", array)
        return k
    }

    companion object {
        fun coordsInBounds(x: Int, y: Int): Boolean {
            return x in 0..BOARD_SIZE && y in 0..BOARD_SIZE
        }

        sealed class MoveResult {
            object Miss : MoveResult()
            object Hit : MoveResult()
        }
    }
}