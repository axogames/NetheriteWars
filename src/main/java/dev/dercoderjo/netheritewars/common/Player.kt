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

fun checkPositionInBorders(plugin: NetheriteWars, player: Player) {
    val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")
    val dbPlayer = plugin.DATABASE.getPlayer(player.uniqueId.toString())

    fun showBorderBossBar(player: Player) {
        player.showBossBar(BossBar.bossBar(Component.text("Grenzgebiet"), ((borderSize - abs(player.location.z)) / borderSize).toFloat(), BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS))
    }
    fun showOpponenntBossBar(player: Player) {
        var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
        if (progress > 1) {
            progress = 1f
        }
        player.showBossBar(BossBar.bossBar(Component.text("Feindgebiet"), progress, BossBar.Color.RED, BossBar.Overlay.PROGRESS))
    }
    fun showHomeBossBar(player: Player) {
        var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
        if (progress > 1) {
            progress = 1f
        }
        player.showBossBar(BossBar.bossBar(Component.text("Heimatgebiet"), progress, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS))
    }

    if (dbPlayer.team == Teams.RED) {
        player.scoreboard.getTeam("red")?.addEntry(player.name)
    } else if (dbPlayer.team == Teams.BLUE) {
        player.scoreboard.getTeam("blue")?.addEntry(player.name)
    }

    val bossbars = player.activeBossBars().toList().toList()
    for (bossbar in bossbars) {
        player.hideBossBar(bossbar)
    }

    if (abs(player.location.z) <= borderSize || player.world.environment != World.Environment.NORMAL) {
        if (dbPlayer.position != Position.BORDER) {
            sendMessage(player, "Du bist nun im Grenzgebiet")
            dbPlayer.position = Position.BORDER
            if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                player.gameMode = GameMode.ADVENTURE
            }

        }
        showBorderBossBar(player)
    } else if (player.location.z > 0) {
        if (dbPlayer.team == Teams.RED) {
            if (dbPlayer.position != Position.BLUE) {
                dbPlayer.position = Position.BLUE
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.ADVENTURE
                }
                sendMessage(player, "Du bist nun im Feindgebiet")
            }
            if (player.isGliding) {
                player.isGliding = false
            }
            showOpponenntBossBar(player)
        } else if (dbPlayer.team == Teams.BLUE) {
            if (dbPlayer.position != Position.BLUE) {
                dbPlayer.position = Position.BLUE
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.SURVIVAL
                }
                sendMessage(player, "Du bist nun im Heimatgebiet")
            }
            showHomeBossBar(player)
        }

    } else if (player.location.z < 0) {
        if (dbPlayer.team == Teams.BLUE) {
            if (dbPlayer.position != Position.RED) {
                dbPlayer.position = Position.RED
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.ADVENTURE
                }
                sendMessage(player, "Du bist nun im Feindgebiet")
            }
            showOpponenntBossBar(player)
            if (player.isGliding) {
                player.isGliding = false
            }
        } else if (dbPlayer.team == Teams.RED && dbPlayer.position != Position.RED) {
            if (dbPlayer.position != Position.RED) {
                dbPlayer.position = Position.RED
                sendMessage(player, "Du bist nun im Heimatgebiet")
                player.gameMode = GameMode.SURVIVAL
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.SURVIVAL
                }
            }
            sendMessage(player, "Du bist nun im Heimatgebiet")
            player.gameMode = GameMode.SURVIVAL
            showHomeBossBar(player)
        }
    }
    plugin.DATABASE.setPlayer(dbPlayer)
}