package ru.ifmo.telegram.bot.services.main

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import ru.ifmo.services.game.tictactoe.TTTGameFactory
import ru.ifmo.services.game.tictactoe.TTTStepFactory
import ru.ifmo.telegram.bot.services.game.Game
import ru.ifmo.telegram.bot.services.game.GameFactory
import ru.ifmo.telegram.bot.services.game.Step

@Service
class MainGameFactory() {
    val gameFactoris = mutableMapOf(Pair(Games.TTT, TTTGameFactory()))

    fun getGameFactory(name: String) = gameFactoris[Games.valueOf(name)]
}

@Service
class MainStepFactory {
    val stepFactoris = mutableMapOf(Pair(Games.TTT, TTTStepFactory()))

    fun getStepFactory(name: String) = stepFactoris[Games.valueOf(name)]
}

enum class Games {
    TTT
}