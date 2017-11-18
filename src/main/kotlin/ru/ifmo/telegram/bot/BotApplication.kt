package ru.ifmo.telegram.bot

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class BotApplication

fun main(args: Array<String>) {
    //Registrator.registrate()
    SpringApplication.run(BotApplication::class.java, *args)
}
