package dev.dercoderjo.netheritewars.common

class BattleRoyal(
    val status: BattleRoyalStatus?,
    val endsAt: Long?,
    val pausedAt: Long?)
    {}

enum class BattleRoyalStatus {
    PREPARED,
    STARTED,
    ENDED,
    PAUSED
}