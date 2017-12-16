package ru.ifmo.telegram.bot.services.game.blingame.logic

class SBGame() {
    val myBoard1: MyBoard = MyBoard()
    val myBoard2: MyBoard = MyBoard()
    val enemyBoard1: EnemyBoard = EnemyBoard()
    val enemyBoard2: EnemyBoard = EnemyBoard()
    val myBoards = listOf<MyBoard>(myBoard1, myBoard2)
    val enemyBoards = listOf<EnemyBoard>(enemyBoard1, enemyBoard2)
    var state: GameState = GameState.PlacingShips

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

        sealed class GameState {
            object PlacingShips : GameState()
            data class PlayerTurn(val playerId: Int) : GameState()
            data class GameEnded(val winnerId: Int) : GameState()
        }
    }
}