package ru.ifmo.telegram.bot.services.game.blingame.logic

import com.google.gson.JsonObject

class EnemyBoard : Board {
    constructor() : super()
    constructor(json : JsonObject) : super(json)

    fun markTile(x: Int, y:Int, moveResult: MoveResult) {
        val tile = board[x][y]
        when(moveResult) {
            is MoveResult.Miss -> if (tile is Tile.Empty) board[x][y] = Tile.Missed
            is MoveResult.Hit -> board[x][y] = Tile.EnemyShipTile
        }
    }
}