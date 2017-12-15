package ru.ifmo.telegram.bot.services.game.blingame

import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.game.GameFactory

class SeaBattleGameFactory : GameFactory<SeaBattleStep, SeaBattleGame> {
    override fun maxNumberPlayers(): Int = 2

    override fun minNumberPlayers(): Int = 2

    override fun getGame(vararg player: Player): SeaBattleGame {
        return SeaBattleGame(player[0], player[1])
    }

    override fun getInfo(): String {
        return "Super awesome great game for people who are cool and support Navalny"
    }

    override fun fromJson(json: String): SeaBattleGame {
        TODO()
    }
}

