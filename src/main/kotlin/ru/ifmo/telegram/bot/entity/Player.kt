package ru.ifmo.telegram.bot.entity

import javax.persistence.*

@Entity
@Table(indexes = [(Index(columnList = "chatId", unique = true)), (Index(columnList = "name", unique = true))], name = "player")
data class Player(@Id @GeneratedValue var id: Long? = null,
                  val name: String? = null,
                  val chatId: Long? = null,
                  @ManyToOne(fetch = FetchType.EAGER) var game: Game? = null,
                  @ManyToOne(fetch = FetchType.EAGER) var privateGame: PrivateGame? = null)

