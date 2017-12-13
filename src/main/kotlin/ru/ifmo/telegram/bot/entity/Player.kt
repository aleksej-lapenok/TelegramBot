package ru.ifmo.telegram.bot.entity

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.*

@Entity
@Table(indexes = [(Index(columnList = "chatId", unique = true))], name = "player")
data class Player(@Id @GeneratedValue var id: Long? = null,
                  val name: String?,
                  val chatId: Long?)

