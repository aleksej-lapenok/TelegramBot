package ru.ifmo.telegram.bot.services.game.blingame

sealed class Tile {
    object Empty : Tile()
    object Missed : Tile()
    class MyShipTile(var isWrecked: Boolean = false,
                     var ship: MyShip = nullShip) : Tile()
    object EnemyShipTile: Tile()
}