package ru.ifmo.telegram.bot.services.game.blingame

import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.game.StepFactory
import ru.ifmo.telegram.bot.services.game.blingame.SeaBattleStep

class SeaBattleStepFactory : StepFactory<SeaBattleStep> {
    override fun getStep(str: String, p: Player): SeaBattleStep {
        return SeaBattleStep(p, str)
    }
}