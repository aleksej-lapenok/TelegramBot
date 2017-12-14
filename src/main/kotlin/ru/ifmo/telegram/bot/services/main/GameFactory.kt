package ru.ifmo.telegram.bot.services.main

import org.springframework.stereotype.Service
import ru.ifmo.services.game.tictactoe.TTTGameFactory
import ru.ifmo.services.game.tictactoe.TTTStepFactory

@Service
class MainGameFactory {
    val gameFactories = mutableMapOf(Pair(Games.TTT, TTTGameFactory()))

    fun getGameFactory(name: String) = getGameFactory(Games.valueOf(name))

    fun getGameFactory(games: Games) = gameFactories[games]

    val stepFactories = mutableMapOf(Pair(Games.TTT, TTTStepFactory()))

    fun getStepFactory(name: String) = getStepFactory(Games.valueOf(name))

    fun getStepFactory(games: Games) = stepFactories[games]
}

enum class Games {
    TTT,
    CHECKERS
}