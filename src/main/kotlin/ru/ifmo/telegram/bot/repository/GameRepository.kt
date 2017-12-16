package ru.ifmo.telegram.bot.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.ifmo.telegram.bot.entity.Game
import ru.ifmo.telegram.bot.entity.PrivateGame

@Repository
interface GameRepository : CrudRepository<Game, Long>

@Repository
interface PrivateGameRepository : CrudRepository<PrivateGame, Long>