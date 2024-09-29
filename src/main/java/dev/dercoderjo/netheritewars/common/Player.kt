package dev.dercoderjo.netheritewars.common

import dev.dercoderjo.netheritewars.util.getNetheriteItem
import org.bukkit.Material
import org.bukkit.entity.Player

class Player(
    val uuid: String,
    var netherite: Int,
    val deaths: Int,
    var position: Position,
    val team: Teams,
    val whitelisted: Boolean,
    val orga: Boolean)
    {}

fun checkInventoryForNetherite(player: Player): Int {
    var netheriteCount = 0

    for (item in player.inventory.contents) {
        if (item?.type == Material.NETHERITE_INGOT) {
            netheriteCount += item.amount
        } else if (item?.type == Material.NETHERITE_BLOCK) {
            netheriteCount += item.amount * 9
        }
    }
    return netheriteCount
}

fun dropNetherite(player: Player, looseAll: Boolean = true) : Int {
    var netheriteCount = checkInventoryForNetherite(player)

    if (netheriteCount > 16) {
        val droppingNetheriteCount = netheriteCount

        for (item in player.inventory.contents) {
            if (item?.type == Material.NETHERITE_BLOCK) {
                netheriteCount -= item.amount * 9
                item.amount = 0
            }

            if (netheriteCount <= 16 && !looseAll) {
                break
            }

            if (item?.type == Material.NETHERITE_INGOT) {
                netheriteCount -= item.amount
                item.amount = 0
            }

            if (netheriteCount <= 16 && !looseAll) {
                break
            }
        }

        if (netheriteCount < 16 && !looseAll) {
            player.inventory.addItem(getNetheriteItem(16 - netheriteCount))
        }
        return droppingNetheriteCount
    }
    return 0
}

