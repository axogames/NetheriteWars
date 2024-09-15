package dev.dercoderjo.netheritewars.common

class Player(
    val uuid: String,
    var netherite: Int,
    val deaths: Int,
    var position: Position,
    val team: Teams,
    val whitelisted: Boolean,
    val orga: Boolean)
    {}