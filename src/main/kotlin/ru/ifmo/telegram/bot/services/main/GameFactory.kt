package ru.ifmo.telegram.bot.services.main

import org.springframework.stereotype.Service
import ru.ifmo.services.game.checkers.CheckersGameFactory
import ru.ifmo.services.game.checkers.CheckersStepFactory
import ru.ifmo.services.game.pairs.PairsGameFactory
import ru.ifmo.services.game.pairs.PairsStepFactory
import ru.ifmo.services.game.tictactoe.TTTGameFactory
import ru.ifmo.services.game.tictactoe.TTTStepFactory
import ru.ifmo.telegram.bot.services.game.GameFactory
import ru.ifmo.telegram.bot.services.game.StepFactory
import ru.ifmo.telegram.bot.services.game.blingame.SeaBattleGameFactory
import ru.ifmo.telegram.bot.services.game.blingame.SeaBattleStepFactory

@Service
class MainGameFactory {
    val gameFactories = mutableMapOf<Games, GameFactory<*, *>>(
            Pair(Games.TTT, TTTGameFactory()),
            Pair(Games.CHECKERS, CheckersGameFactory()),
            Pair(Games.SEABATTLE, SeaBattleGameFactory()),
            Pair(Games.PAIRS, PairsGameFactory()))

    fun getGameFactory(name: String) = getGameFactory(Games.valueOf(name))

    fun getGameFactory(games: Games) = gameFactories[games]

    val stepFactories = mutableMapOf<Games, StepFactory<*>>(
            Pair(Games.TTT, TTTStepFactory()),
            Pair(Games.CHECKERS, CheckersStepFactory()),
            Pair(Games.SEABATTLE, SeaBattleStepFactory()),
            Pair(Games.PAIRS, PairsStepFactory()))

    fun getStepFactory(name: String) = getStepFactory(Games.valueOf(name))

    fun getStepFactory(games: Games) = stepFactories[games]
}

enum class Games {
    TTT,
    SEABATTLE,
    CHECKERS,
    PAIRS
}