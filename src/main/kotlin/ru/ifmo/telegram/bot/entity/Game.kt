package ru.ifmo.telegram.bot.entity

import ru.ifmo.telegram.bot.services.main.Games
import javax.persistence.*
import javax.validation.constraints.NotNull

@Entity
data class Game(@Id @GeneratedValue var id: Long = 0,
                @Lob var json: String = "",
                @Enumerated(EnumType.STRING) var game: Games = Games.TTT) {

    override fun hashCode(): Int = id.toInt()

    override fun equals(other: Any?): Boolean {
        return (other as PrivateGame).id == id
    }

    override fun toString(): String {
        return game.name
    }
}

@Entity
data class PrivateGame(@Id @GeneratedValue var id: Long = 0,
                       @Enumerated(EnumType.STRING) var game: Games = Games.TTT,
                       @OneToMany(fetch = FetchType.LAZY, mappedBy = "privateGame", cascade = [CascadeType.ALL]) var players: MutableSet<Player> = mutableSetOf(),
                       @ManyToMany(fetch = FetchType.LAZY) var invitations: MutableSet<Player> = mutableSetOf(),
                       @NotNull @ManyToOne(fetch = FetchType.LAZY) var creator: Player? = null) {

    override fun hashCode(): Int = id.toInt()

    override fun equals(other: Any?): Boolean {
        return (other as PrivateGame).id == id
    }

    override fun toString(): String {
        return game.name
    }
}