package ru.ifmo.telegram.bot.entity

import lombok.AllArgsConstructor
import lombok.Builder
import lombok.Data
import lombok.NoArgsConstructor
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.Id

@Entity
@Builder
public data class Player(@Id val id: Long? = null,
                      val name: String,
                      val chatId: Long)

