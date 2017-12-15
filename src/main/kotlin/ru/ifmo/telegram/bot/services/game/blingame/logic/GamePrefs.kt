package ru.ifmo.telegram.bot.services.game.blingame.logic

object GamePrefs {
    val BOARD_SIZE = 10
    val SHIP_TYPES_COUNT = mapOf<Int, Int>(
            Pair(1, 4),
            Pair(2, 3),
            Pair(3, 2),
            Pair(4, 1)
    )
}