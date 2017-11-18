package ru.ifmo.telegram.bot.services.game

interface Game<T> {
    fun step(step: Step<T>)
    val board:Array<Array<T>>
    fun finish()
}

interface Step<T> {
    val cell:Cell<T>
}

interface Cell<T> {
    val x:Int
    val y:Int
    val state:T
}