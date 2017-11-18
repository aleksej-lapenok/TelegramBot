package ru.ifmo.telegram.bot.controller

import com.sun.deploy.net.HttpResponse
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PatchMapping

@Controller
class MainController {

    @PatchMapping("/")
    fun main(response:HttpResponse) {

    }
}