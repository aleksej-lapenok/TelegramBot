package ru.ifmo.telegram.bot.services.game

import ru.ifmo.services.game.GameUpdate
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.main.Games
import ru.ifmo.telegram.bot.services.telegramApi.classes.Keyboard
import java.io.File

/**
 *
 */
interface Game<in T : Step> {
    /**
     * accept step and change state of game
     */
    fun step(step: T): Pair<String, Boolean>

    /**
     * return state of game
     */
    fun drawPicture(player: Player): File

    /**
     * возвращает то, что надо делать игроку
     */
    fun getMessage(player: Player): String


    fun getKeyboard(player: Player): Keyboard


    fun getGameUpdate(player: Player): GameUpdate

    /**
     * surrender game
     */
    fun surrender(player: Player)

    /**
     * return json of game
     */
    fun toJson(): String

    /**
     * return playes in game
     */
    fun getPlayes(): List<Player>

    fun getGameId(): Games

    fun isFinished(): Boolean

    fun isCurrent(player: Player): Boolean

}


interface Step {
    /**
     * who made this step
     */
    val player: Player
}
