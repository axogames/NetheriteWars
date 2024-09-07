package dev.dercoderjo.netheritewars.util

import dev.dercoderjo.netheritewars.common.Teams
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material

fun countTeamNetherite(team: Teams): Int {
    var netheriteCount = 0

    for (x in 490..510) {
        for (y in 70..320) {
            for (z in 490..510) {
                val location = Location(Bukkit.getWorld("world"), x.toDouble(), y.toDouble(), z.toDouble())
                if (location.block.type == Material.NETHERITE_BLOCK) {
                    netheriteCount += 1
                }
            }
        }
    }

    return netheriteCount
}
