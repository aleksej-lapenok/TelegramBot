package ru.ifmo.telegram.bot.services.game.blingame

import com.google.gson.JsonParser
import ru.ifmo.services.game.GameException
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

    override fun fromJson(json: String, vararg player: Player): SeaBattleGame {
        assert(player.size == 2)
        val player1 = player[0]
        val player2 = player[1]

        val parser = JsonParser()
        val gameJson = parser.parse(json).getAsJsonObject()

        val p1 : Player;
        val p2 : Player;
        if (gameJson.get("p1").asLong == player1.id && gameJson.get("p2").asLong == player2.id) {
            p1 = player1
            p2 = player2
        } else {
            if (gameJson.get("p2").asLong == player1.id && gameJson.get("p1").asLong == player2.id) {
                p2 = player1
                p1 = player2
            } else {
                throw GameException("Wrong players for deserialization")
            }
        }
        val seaBattleGame = SeaBattleGame(p1, p2, gameJson.get("game").asJsonObject)
        return seaBattleGame
    }
}

