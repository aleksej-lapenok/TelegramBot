package ru.ifmo.telegram.bot.services.game.blingame

import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.game.Step

class SeaBattleStep(override val player: Player, val command: String) : Step