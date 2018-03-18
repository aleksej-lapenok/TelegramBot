package ru.ifmo.telegram.bot

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.scheduling.annotation.EnableScheduling
import ru.ifmo.telegram.bot.entity.Player
import ru.ifmo.telegram.bot.services.main.Games
import ru.ifmo.telegram.bot.services.main.MainGameFactory

@SpringBootApplication
@EnableScheduling
open class BotApplication

fun main(args: Array<String>) {
    //Registrator.registrate()
//    SpringApplication.run(BotApplication::class.java, *args)
    val ctx = AnnotationConfigApplicationContext(ContextConfiguration::class.java)

    val factory = ctx.getBean(MainGameFactory::class.java)

    val gameFactory = factory.getGameFactory(Games.SEABATTLE)
    val game = gameFactory!!.getGame(Player(0, "name1", 0), Player(1, "name2", 1))

    print("game: ${game.toJson()}")

}
