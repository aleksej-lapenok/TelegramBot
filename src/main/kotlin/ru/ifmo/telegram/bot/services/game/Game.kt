package ru.ifmo.telegram.bot.services.game

import ru.ifmo.telegram.bot.entity.Player
import java.awt.image.BufferedImage

/**
 *
 */
interface Game<in T : Step> {
    /**
     * accept step and change state of game
     */
    fun step(step: T): String

    /**
     * return state of game
     */
    fun drawPicture(player: Player): Array<Byte>

    /**
     * возвращает то, что надо делать игроку
     */
    fun getMessage(player: Player): String

    /**
     * finish game
     */
    fun finish()

    /**
     * return json of game
     */
    fun toJson(): String
}


interface Step {
    /**
     * who made this step
     */
    val player: Player
}
