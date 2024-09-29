package dev.dercoderjo.netheritewars.common

import dev.dercoderjo.netheritewars.NetheriteWars
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.abs

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

val borderBossBarName = Component.text("Grenzgebiet")
val opponentBossBarName = Component.text("Feindgebiet")
val homeBossBarName = Component.text("Heimatgebiet")

fun checkPositioninBorders(plugin: NetheriteWars, player: Player) {
    val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")
    val dbPlayer = plugin.DATABASE.getPlayer(player.uniqueId.toString())

    if (player.gameMode != GameMode.SURVIVAL && player.gameMode != GameMode.ADVENTURE) {
        return
    }

    if (dbPlayer.team == Teams.RED) {
        player.scoreboard.getTeam("red")?.addEntry(player.name)
    } else if (dbPlayer.team == Teams.BLUE) {
        player.scoreboard.getTeam("blue")?.addEntry(player.name)
    }

    for (bossBar in player.activeBossBars()) {
        if (bossBar.name() == borderBossBarName) {
            player.hideBossBar(bossBar)
        }
    }

    if (abs(player.location.z) <= borderSize || player.world.environment != World.Environment.NORMAL) {
        if ((dbPlayer.team == Teams.RED && dbPlayer.position == Position.BLUE) || (dbPlayer.team == Teams.BLUE && dbPlayer.position == Position.RED)) {
            for (bossBar in player.activeBossBars()) {
                if (bossBar.name() == opponentBossBarName) {
                    player.hideBossBar(bossBar)
                }
            }
        }

        if (dbPlayer.position != Position.BORDER) {
            sendMessage(player, "Du bist nun im Grenzgebiet")
            player.showBossBar(BossBar.bossBar(borderBossBarName, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
            dbPlayer.position = Position.BORDER
            player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
        }

        val progress = if (player.world.environment == World.Environment.NORMAL) {
            ((borderSize - abs(player.location.z)) / borderSize).toFloat()
        } else {
            1.0f
        }
        val bossBar = BossBar.bossBar(
            borderBossBarName,
            progress,
            BossBar.Color.YELLOW,
            BossBar.Overlay.PROGRESS
        )

        player.showBossBar(bossBar)
    } else if (player.location.z > 0) {
        if (dbPlayer.team == Teams.RED) {
            if (dbPlayer.position != Position.BLUE) {
                player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                sendMessage(player, "Du bist nun im Feindgebiet")
                player.showBossBar(BossBar.bossBar(opponentBossBarName, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
                dbPlayer.position = Position.BLUE
                if (player.gameMode == GameMode.SURVIVAL) player.gameMode = GameMode.ADVENTURE
            }

            if (player.isGliding) {
                player.isGliding = false
            }
        } else if (dbPlayer.team == Teams.BLUE && dbPlayer.position != Position.BLUE) {
            player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
            sendMessage(player, "Du bist nun im Heimatgebiet")
            player.showBossBar(BossBar.bossBar(homeBossBarName, 1.0f, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS))
            dbPlayer.position = Position.BLUE
            if (player.gameMode == GameMode.ADVENTURE) player.gameMode = GameMode.SURVIVAL
        }
    } else if (player.location.z < 0) {
        if (dbPlayer.team == Teams.BLUE) {
            if (dbPlayer.position != Position.RED) {
                player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                sendMessage(player, "Du bist nun im Feindgebiet")
                player.showBossBar(
                    BossBar.bossBar(opponentBossBarName, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS
                )
                )
                dbPlayer.position = Position.RED
                if (player.gameMode == GameMode.SURVIVAL) player.gameMode = GameMode.ADVENTURE
            }

            if (player.isGliding) {
                player.isGliding = false
            }
        } else if (dbPlayer.team == Teams.RED && dbPlayer.position != Position.RED) {
            player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
            sendMessage(player, "Du bist nun im Heimatgebiet")
            player.showBossBar(BossBar.bossBar(homeBossBarName, 1.0f, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
            dbPlayer.position = Position.RED
            if (player.gameMode == GameMode.ADVENTURE) player.gameMode = GameMode.SURVIVAL
        }
    }

    plugin.DATABASE.setPlayer(dbPlayer)
}