package ru.ifmo.telegram.bot.services.telegramApi.classes


/**
 * Created by Cawa on 18.11.2017.
 */
data class Update(
        var type: TypeUpdate,
        var data: String,
        var name: String?=null,
        var chatId: Long,
        var userId: Long,
        var update_id: Long,
        var message_id: Long)

enum class TypeUpdate {
    MESSAGE,
    CALLBACK_QUERY
}
