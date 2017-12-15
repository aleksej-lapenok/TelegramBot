package ru.ifmo.telegram.bot.services.game.blingame.logic

import ru.ifmo.telegram.bot.services.game.blingame.logic.GamePrefs.BOARD_SIZE

abstract class Board {
    protected val board = MutableList(GamePrefs.BOARD_SIZE, { MutableList<Tile>(GamePrefs.BOARD_SIZE, { Tile.Empty }) })

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

    companion object {
        fun coordsInBounds(x: Int, y: Int): Boolean {
            return x in 0..BOARD_SIZE && y in 0..BOARD_SIZE
        }

        sealed class MoveResult {
            object Miss : MoveResult()
            data class Hit(val isKilled: Boolean = false) : MoveResult()
        }
    }
}