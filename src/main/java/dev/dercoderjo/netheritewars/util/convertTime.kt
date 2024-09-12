package dev.dercoderjo.netheritewars.util

import java.time.Duration

fun convertTime(time: Long): String {
    val duration = Duration.ofMillis(time)
    val hours = duration.toHours()
    val minutes = duration.toMinutes() % 60
    val seconds = duration.seconds % 60

    return if (hours > 0) {
        String.format("%02d:%02d:%02d", hours, minutes, seconds)
    } else {
        String.format("%02d:%02d", minutes, seconds)
    }
}