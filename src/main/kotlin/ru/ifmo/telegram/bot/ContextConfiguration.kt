package ru.ifmo.telegram.bot

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.EnableAspectJAutoProxy
import ru.ifmo.telegram.bot.services.main.MainGameFactory

@Configuration
@EnableAspectJAutoProxy
open class ContextConfiguration {

    @Bean
    open fun updateCollector(): MainGameFactory = MainGameFactory()
}