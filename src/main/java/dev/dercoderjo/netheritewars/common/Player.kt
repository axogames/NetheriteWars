package dev.dercoderjo.netheritewars.common

import dev.dercoderjo.netheritewars.NetheriteWars
import net.kyori.adventure.bossbar.BossBar
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.World
import org.bukkit.block.Block
import org.bukkit.entity.Player
import kotlin.math.abs

class Player(
    val uuid: String,
    var netherite: Int,
    var deaths: Int,
    var position: Position,
    val team: Teams,
    val whitelisted: Boolean,
    val orga: Boolean)
    {}

/**
 * Zählt die Anzahl der Netheriteingots- und Blöcke im Inventar eines Spielers.
 *
 * @param player Der Spieler, dessen Inventar durchsucht werden soll
 * @return 9xAnzahl Blöcke + Anzahl Ingots
 */
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

/**
 * Droppt das Netherite aus dem Inventar eines Spielers.
 * Unabhöngig von der Anzahl werden immer mindestens vier gedroppt.
 *
 * @param player Der Spieler, dessen Netherite gedroppt werden soll
 * @param looseAll Wahrheitswert, ob der Spieler all sein Netherite verlieren soll oder bis zu 16 behält
 * @return Die Anzahl an Netheriteingots, die der Spieler fallen lässt
 */
fun dropNetherite(player: Player, looseAll: Boolean = true) : Int {
    var netheriteCount = checkInventoryForNetherite(player)

    if (netheriteCount > 16 || looseAll) {
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
    return 4
}

/**
 * Zeigt einem Spieler entsprechend seiner Position in der Welt eine Bossbar an.
 *
 * @param plugin Das Basisplugin von NetheriteWars
 * @param player Der Spieler, dessen Bossbar aktualisiert werden soll
 */
fun checkPositionInBorders(plugin: NetheriteWars, player: Player) {
    val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")
    val dbPlayer = plugin.DATABASE.getPlayer(player.uniqueId.toString())

    fun showBorderBossBar(player: Player) {
        player.showBossBar(BossBar.bossBar(Component.text("Grenzgebiet"), ((borderSize - abs(player.location.z)) / borderSize).toFloat(), BossBar.Color.YELLOW, BossBar.Overlay.PROGRESS))
    }
    fun showRedBossBar(player: Player) {
        var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
        if (progress > 1) {
            progress = 1f
        }
        player.showBossBar(BossBar.bossBar(Component.empty(), progress,BossBar.Color.RED, BossBar.Overlay.PROGRESS))
    }
    fun showBlueBossBar(player: Player) {
        var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
        if (progress > 1) {
            progress = 1f
        }
        player.showBossBar(BossBar.bossBar(Component.empty(), progress, BossBar.Color.BLUE, BossBar.Overlay.PROGRESS))
    }

    if (dbPlayer.team == Teams.RED) {
        player.scoreboard.getTeam("red")?.addEntry(player.name)
    } else if (dbPlayer.team == Teams.BLUE) {
        player.scoreboard.getTeam("blue")?.addEntry(player.name)
    }


    if (player.world.environment != World.Environment.NORMAL) {
        plugin.DATABASE.setPlayer(dbPlayer)
        return
    }

    var activeBossBar : BossBar = player.activeBossBars().first()
    var bossbarTeam : Teams = Teams.UNSET
    for (bossbar in player.activeBossBars()) {
        if (bossbar.name() == Component.text("Grenzgebiet")) {
            activeBossBar = bossbar
            break
        } else if(bossbar.name() == Component.empty()) {
            if (bossbar.color() == BossBar.Color.RED) {
                activeBossBar = bossbar
                bossbarTeam = Teams.RED
                break
            } else if (bossbar.color() == BossBar.Color.BLUE) {
                activeBossBar = bossbar
                bossbarTeam = Teams.BLUE
                break
            }
        }
    }

    if (abs(player.location.z) <= borderSize) {
        if (bossbarTeam != Teams.UNSET) {
            dbPlayer.position = Position.BORDER
            sendMessage(player, "Du bist nun im Grenzgebiet")
            if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                player.gameMode = GameMode.ADVENTURE
            }
            player.hideBossBar(activeBossBar)
            showBorderBossBar(player)
        } else {
            activeBossBar.progress(((borderSize - abs(player.location.z)) / borderSize).toFloat())
        }
    } else if (player.location.z > 0) {
        if (bossbarTeam != Teams.BLUE) {
            dbPlayer.position = Position.BLUE
            if (dbPlayer.team == Teams.RED) {
                sendMessage(player, "Du bist nun im Feindgebiet")
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.ADVENTURE
                } else {
                    sendMessage(player, "Du bist nun im Heimatgebiet")
                }
            }
            player.hideBossBar(activeBossBar)
            showBlueBossBar(player)
        } else {
            var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
            if (progress > 1) {
                progress = 1f
            }
            activeBossBar.progress(progress)
        }
    } else {
        if (bossbarTeam != Teams.RED) {
            dbPlayer.position = Position.RED
            if (dbPlayer.team == Teams.BLUE) {
                sendMessage(player, "Du bist nun im Feindgebiet")
                if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                    player.world.spawnParticle(Particle.FIREWORK, player.location, 64, 0.0, 0.0, 0.0, 0.25)
                    player.gameMode = GameMode.ADVENTURE
                } else {
                    sendMessage(player, "Du bist nun im Heimatgebiet")
                }
            }
            player.hideBossBar(activeBossBar)
            showRedBossBar(player)
        } else {
            var progress = ((abs(player.location.z) - borderSize) / borderSize).toFloat()
            if (progress > 1) {
                progress = 1f
            }
            activeBossBar.progress(progress)
        }
    }
    plugin.DATABASE.setPlayer(dbPlayer)
}

/**
 * Prüft, ob irgendein Spieler innerhalb eines Blockes steht.
 *
 * @param block Der Block, der überprüft werden soll
 * @return Ob ein Spieler in diesem Block steht
 */
fun isPlayerInBlock(block: Block): Boolean {
    val blockLocation = block.location
    val world = block.world

    val nearbyEntities = world.getNearbyEntities(blockLocation, 1.0, 1.0, 1.0)

    for (entity in nearbyEntities) {
        if (entity is Player) {
            val playerLocation = entity.location
            if (playerLocation.blockX == blockLocation.blockX && (playerLocation.blockY == blockLocation.blockY || playerLocation.blockY == blockLocation.blockY - 1) && playerLocation.blockZ == blockLocation.blockZ) {
                return true
            }
        }
    }

    return false
}
