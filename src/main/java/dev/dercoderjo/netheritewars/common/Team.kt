package dev.dercoderjo.netheritewars.common

class Team(
    val team: Teams,
    var netherite: Int)
    {}

enum class Teams {
    BLUE,
    RED,
    UNSET
}