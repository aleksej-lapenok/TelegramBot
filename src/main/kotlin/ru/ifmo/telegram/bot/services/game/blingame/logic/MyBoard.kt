package ru.ifmo.telegram.bot.services.game.blingame.logic

import ru.ifmo.telegram.bot.services.game.blingame.logic.GamePrefs.BOARD_SIZE

class MyBoard() : Board() {
    private val ships = mutableListOf<MyShip>()
    private val shipsRemainToPlace = GamePrefs.SHIP_TYPES_COUNT.toMutableMap()

    fun getShipsRemainToPlace(): Map<Int, Int> {
        return shipsRemainToPlace
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
        ships.add(MyShip(tiles))
        for ((i, p) in coordsList.withIndex()) {
            val (xx, yy) = p
            board[xx][yy] = tiles[i]
        }
        shipsRemainToPlace[size] = shipsCount - 1
        return ShipPlaceResult.ShipPlaceOK
    }

    fun isAllShipsPlaced(): Boolean = shipsRemainToPlace.values.sum() == 0

    fun getBoardAndShips(): Pair<List<List<Tile>>, List<MyShip>> {
        return Pair(board, ships)
    }

    fun isAllShipsWrecked() = ships.all { it.isWrecked() }

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
                    if (tile.ship.isWrecked()) {
                        MoveResult.Hit(true)
                    } else {
                        MoveResult.Hit(false)
                    }
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
