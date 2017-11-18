package ru.ifmo.telegram.bot.services.game

import ru.ifmo.telegram.bot.entity.Player

interface GameFactory<T, out E:Game<T>> {
    fun maxPlayers():Int
    fun minPlayers():Int
    fun getGame(vararg player: Player):E
}