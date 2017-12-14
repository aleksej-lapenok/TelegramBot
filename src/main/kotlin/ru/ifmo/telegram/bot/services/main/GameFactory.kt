package ru.ifmo.telegram.bot.services.main

import org.springframework.stereotype.Service
import ru.ifmo.services.game.tictactoe.TTTGameFactory
import ru.ifmo.services.game.tictactoe.TTTStepFactory

@Service
class MainGameFactory {
    val gameFactoris = mutableMapOf(Pair(Games.TTT, TTTGameFactory()))

    fun getGameFactory(name: String) = getGameFactory(Games.valueOf(name))

    fun getGameFactory(games: Games) = gameFactoris[games]

    val stepFactoris = mutableMapOf(Pair(Games.TTT, TTTStepFactory()))

    fun getStepFactory(name: String) = getStepFactory(Games.valueOf(name))

    fun getStepFactory(games: Games) = stepFactoris[games]
}

enum class Games {
    TTT
}