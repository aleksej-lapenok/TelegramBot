package ru.ifmo.telegram.bot.entity

import ru.ifmo.telegram.bot.services.main.Games
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class Game(@Id @GeneratedValue var id: Long? = null,
                @Lob var json: String = "",
                @Enumerated(EnumType.STRING) var game: Games,
                @OneToMany(fetch = FetchType.LAZY, mappedBy = "game") var players: Set<Player>)

@Entity
data class PrivateGame(@Id @GeneratedValue var id: Long? = null,
                       @Enumerated(EnumType.STRING) var game: Games,
                       @OneToMany(fetch = FetchType.LAZY, mappedBy = "privateGame") var players: MutableSet<Player> = mutableSetOf(),
                       @ManyToMany var invitations: MutableSet<Player> = mutableSetOf(),
                       @NotNull @ManyToOne(fetch = FetchType.EAGER) var creator: Player? = null) {
}