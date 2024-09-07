package dev.dercoderjo.netheritewars.util

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.floor

fun checkInventory(player: Player): Int {
    var netheriteCount = 0

    for (item in player.inventory.contents) {
        if (item?.type == Material.NETHERITE_INGOT) {
            netheriteCount += item.amount
        } else if (item?.type == Material.NETHERITE_BLOCK) {
            netheriteCount += item.amount * 9
        }
    }

    if (netheriteCount > 72 && player.isGliding) {
        player.isGliding = false
    }

    val slownessStrength = floor(netheriteCount / 72.0).toInt()
    val weaknessStrength = floor(netheriteCount / 31.0).toInt()
    if (player.getPotionEffect(PotionEffectType.SLOWNESS)?.duration == -1) player.removePotionEffect(PotionEffectType.SLOWNESS)
    if (player.getPotionEffect(PotionEffectType.WEAKNESS)?.duration == -1) player.removePotionEffect(PotionEffectType.WEAKNESS)
    if (slownessStrength > 0) player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, -1, slownessStrength - 1, false, false, true))
    if (weaknessStrength > 0) player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, -1, weaknessStrength - 1, false, false, true))

    return netheriteCount
}
