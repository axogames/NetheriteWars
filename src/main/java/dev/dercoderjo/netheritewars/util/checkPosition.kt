package dev.dercoderjo.netheritewars.util

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.Position
import dev.dercoderjo.netheritewars.common.Teams
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.entity.Player
import kotlin.math.abs


val borderBossBarName = Component.text("Grenze")
val opponentBossBarName = Component.text("Gegnerisches Gebiet")

fun checkPosition(plugin: NetheriteWars, player: Player) {
    val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")
    val dbPlayer = plugin.DATABASE.getPlayer(player.uniqueId.toString())


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
            player.sendMessage(Component.text("Du bist nun im Grenzgebiet!"))
            dbPlayer.position = Position.BORDER
            player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
        }

        var progress = 0.0f
        if (player.world.environment == World.Environment.NORMAL) {
            progress = ((borderSize - abs(player.location.z)) / borderSize).toFloat()
        } else {
            progress = 1.0f
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
                player.sendMessage(Component.text("Du bist nun im gegnerischen Gebiet!"))
                player.showBossBar(
                    BossBar.bossBar(
                        opponentBossBarName,
                        1.0f,
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS
                    )
                )
                dbPlayer.position = Position.BLUE
                if (player.gameMode == GameMode.SURVIVAL) player.gameMode = GameMode.ADVENTURE
            }

            if (player.isGliding) {
                player.isGliding = false
            }
        } else if (dbPlayer.team == Teams.BLUE && dbPlayer.position != Position.BLUE) {
            player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
            player.sendMessage(Component.text("Du bist nun im heimat Gebiet!"))
            dbPlayer.position = Position.BLUE
            if (player.gameMode == GameMode.ADVENTURE) player.gameMode = GameMode.SURVIVAL
        }
    } else if (player.location.z < 0) {
        if (dbPlayer.team == Teams.BLUE) {
            if (dbPlayer.position != Position.RED) {
                player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                player.sendMessage(Component.text("Du bist nun im gegnerischen Gebiet!"))
                player.showBossBar(
                    BossBar.bossBar(
                        opponentBossBarName,
                        1.0f,
                        BossBar.Color.RED,
                        BossBar.Overlay.PROGRESS
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
            player.sendMessage(Component.text("Du bist nun im heimat Gebiet!"))
            dbPlayer.position = Position.RED
            if (player.gameMode == GameMode.ADVENTURE) player.gameMode = GameMode.SURVIVAL
        }
    }

    plugin.DATABASE.setPlayer(dbPlayer)
}
