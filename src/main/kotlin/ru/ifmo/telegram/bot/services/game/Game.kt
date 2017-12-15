package ru.ifmo.telegram.bot.services.game

import ru.ifmo.services.game.GameUpdate
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.main.Games
import java.awt.image.RenderedImage
import java.io.IOException

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
    @Throws(IOException::class)
    fun drawPicture(player: Player): RenderedImage

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
    fun getPlayers(): List<Player>

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
