package ru.ifmo.telegram.bot.repository

import org.springframework.data.repository.CrudRepository
import org.springframework.stereotype.Repository
import ru.ifmo.telegram.bot.entity.Player

@Repository
interface PlayerRepository : CrudRepository<Player, Long> {
}