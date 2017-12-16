package ru.ifmo.telegram.bot.services.game.blingame.logic

import com.google.gson.JsonObject

sealed class Tile {
    abstract fun toJson(): JsonObject

    object Empty : Tile() {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("name", "Empty")
            return json
        }
    }
    object Missed : Tile() {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("name", "Missed")
            return json
        }
    }
    class MyShipTile(var isWrecked: Boolean = false) : Tile() {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("name", "MyShipTile")
            json.addProperty("isWrecked", isWrecked)
            return json
        }
    }
    object EnemyShipTile: Tile() {
        override fun toJson(): JsonObject {
            val json = JsonObject()
            json.addProperty("name", "EnemyShipTile")
            return json
        }
    }

    companion object {
        fun tileFromJson(json: JsonObject) : Tile {
            return when(json.get("name").asString) {
                "Empty" -> Empty
                "Missed" -> Missed
                "MyShipTile" -> MyShipTile(json.get("isWrecked").asBoolean)
                "EnemyShipTile" -> EnemyShipTile
                else -> throw Exception()
            }
        }
    }
}