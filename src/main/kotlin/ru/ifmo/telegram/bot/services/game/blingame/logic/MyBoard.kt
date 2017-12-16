package ru.ifmo.telegram.bot.services.game.blingame.logic

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ru.ifmo.telegram.bot.services.game.blingame.logic.GamePrefs.BOARD_SIZE

class MyBoard : Board {
    private var shipsRemainToPlace = GamePrefs.SHIP_TYPES_COUNT.toMutableMap()

    constructor() : super()

    constructor(gameJson : JsonObject) : super(gameJson) {
        val map = mutableMapOf<Int, Int>()
        for (pair in gameJson.get("shipsRemainToPlace").asJsonArray) {
            val obj = pair.asJsonObject
            val k = obj.get("key").asInt
            val v = obj.get("val").asInt
            map.put(k, v)
        }
        shipsRemainToPlace = map
    }

    fun getShipsRemainToPlace(): Map<Int, Int> {
        return shipsRemainToPlace
    }

    override fun toJson(): JsonObject {
        val json = super.toJson()
        val array = JsonArray()
        for ((k, v) in shipsRemainToPlace) {
            val obj = JsonObject()
            obj.addProperty("key", k)
            obj.addProperty("val", v)
            array.add(obj)
        }
        json.add("shipsRemainToPlace", array)
        return json
    }

    fun placeShip(size: Int,
                  x: Int,
                  y: Int,
                  direction: ShipPlaceDirection): ShipPlaceResult {
        val (dx, dy) = getCoordsByDirection(direction)
        val shipsCount = shipsRemainToPlace.getOrElse(size, { 0 });
        if (shipsCount <= 0) {
            return ShipPlaceResult.NoShipsOfSuchSize
        }
        val coordsList = (0 until size).map { Pair(x+it*dx, y+it*dy) }
        if (!coordsList.all( { (x, y) -> coordsInBounds(x, y)} )) {
            return ShipPlaceResult.OutOfBounds
        }
        if (!coordsList.all { (x, y) -> board[x][y] is Tile.Empty }) {
            return ShipPlaceResult.Intersects
        }
        val tiles = coordsList.map { Tile.MyShipTile() }
        for ((i, p) in coordsList.withIndex()) {
            val (xx, yy) = p
            board[xx][yy] = tiles[i]
        }
        shipsRemainToPlace[size] = shipsCount - 1
        return ShipPlaceResult.ShipPlaceOK
    }

    fun isAllShipsPlaced(): Boolean = shipsRemainToPlace.values.sum() == 0

    fun isAllShipsWrecked() = board.all { it.all { it is Tile.MyShipTile && it.isWrecked } }

    fun makeMove(x: Int, y: Int): MoveResult {
        val tile = board[x][y]
        return when(tile) {
            is Tile.Empty -> run {
                board[x][y] = Tile.Missed
                MoveResult.Miss
            }
            is Tile.Missed -> MoveResult.Miss
            is Tile.MyShipTile -> run {
                if (tile.isWrecked) {
                    MoveResult.Miss
                } else {
                    tile.isWrecked = true
                    MoveResult.Hit
                }
            }
            is Tile.EnemyShipTile -> throw Exception("must not be here")
        }
    }

    private fun getCoordsByDirection(d: ShipPlaceDirection): Pair<Int, Int> {
        // (0,0) is upper-left corner
        return when(d) {
            is ShipPlaceDirection.Right -> Pair(1, 0)
            is ShipPlaceDirection.Left -> Pair(-1, 0)
            is ShipPlaceDirection.Up -> Pair(0, -1)
            is ShipPlaceDirection.Down -> Pair(0, 1)
        }
    }

    companion object {
        sealed class ShipPlaceResult {
            object ShipPlaceOK : ShipPlaceResult()
            object OutOfBounds : ShipPlaceResult()
            object NoShipsOfSuchSize : ShipPlaceResult()
            object Intersects : ShipPlaceResult()
        }

        sealed class ShipPlaceDirection {
            object Right : ShipPlaceDirection()
            object Left : ShipPlaceDirection()
            object Up : ShipPlaceDirection()
            object Down : ShipPlaceDirection()
        }

    }
}
