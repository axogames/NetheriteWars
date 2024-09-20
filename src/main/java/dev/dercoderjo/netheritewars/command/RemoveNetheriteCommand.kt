package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.World
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class RemoveNetheriteCommand(private val plugin: NetheriteWars) : CommandExecutor, TabCompleter {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): MutableList<String> {
        if (args != null && args.size == 1) {
            return mutableListOf("Blau","Rot")
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, alias: String, args: Array<out String>?): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Nur Spieler können diesen Command ausführen!")
            return true
        }
        val player: Player = sender
        if (!plugin.DATABASE.getPlayer(player.uniqueId.toString()).orga) {
            player.sendMessage(Component.text("Das darfst du nicht tun!").color(TextColor.color(255, 0, 0)))
            return true
        }
        if (args == null || args.size != 2) {
            player.sendMessage("Du musst genau zwei Argumente angeben")
            return false
        }

        val minX: Int
        val maxX: Int
        val minZ: Int
        val maxZ: Int
        if (args[0] == "Blau") {
            minX = plugin.CONFIG.getInt("VAULT.BLUE.MINX")
            maxX = plugin.CONFIG.getInt("VAULT.BLUE.MAXX")
            minZ = plugin.CONFIG.getInt("VAULT.BLUE.MINZ")
            maxZ = plugin.CONFIG.getInt("VAULT.BLUE.MAXZ")
        } else if (args[0] == "Rot") {
            minX = plugin.CONFIG.getInt("VAULT.RED.MINX")
            maxX = plugin.CONFIG.getInt("VAULT.RED.MAXX")
            minZ = plugin.CONFIG.getInt("VAULT.RED.MINZ")
            maxZ = plugin.CONFIG.getInt("VAULT.RED.MAXZ")
        } else {
            player.sendMessage("Du musst ein gültiges Team angeben! (Blau oder Rot)")
            return true
        }
        val minY: Int = plugin.CONFIG.getInt("VAULT.MINY")
        val maxY: Int = plugin.config.getInt("VAULT.MAXY")

        var blockCount: Int
        try {
            blockCount = args[1].toInt()
        } catch (exception: Exception) {
            player.sendMessage(args[1] + " ist keine gültige Zahl!")
            return true
        }

        val world: World
        if (Bukkit.getWorld("world") == null) {
            world = Bukkit.getWorlds()[0]
        } else {
            world = Bukkit.getWorld("world")!!
        }

        val findLocation = Location(world, ((minX + maxX)/2).toDouble(), minY.toDouble(), ((minZ + maxZ) / 2).toDouble())
        while (findLocation.block.type == Material.NETHERITE_BLOCK) {
            findLocation.add(0.0, 1.0, 0.0)
        }
        findLocation.add(0.0, -1.0, 0.0)

        val closedList: ArrayList<Location> = ArrayList()
        val openList: ArrayList<Location> = ArrayList()
        var currentLoc: Location
        var adjacentLocs: Array<Location>
        openList.add(findLocation)

        while (blockCount > 0) {
            if (openList.isEmpty()) {
                val newLocation: Location ?= findNewBlock(world, minX, maxX, minY, maxY, minZ, maxZ)
                if (newLocation == null) {
                    if (blockCount > 1) {
                        player.sendMessage("Es konnten $blockCount NetheriteBlöcke nicht entfernt werden.")
                    } else {
                        player.sendMessage("Es konnte 1 Netheriteblock nicht entfernt werden")
                    }

                    return true
                } else {
                    openList.add(newLocation)
                }
            }
            currentLoc = openList.removeAt(0)
            closedList.add(currentLoc)
            adjacentLocs = arrayOf(
                copyLocation(currentLoc).add(1.0, 0.0, 0.0),
                copyLocation(currentLoc).add(-1.0, 0.0, 0.0),
                copyLocation(currentLoc).add(0.0, 0.0, 1.0),
                copyLocation(currentLoc).add(0.0, 0.0, -1.0),
                copyLocation(currentLoc).add(0.0, 1.0, 0.0),
                copyLocation(currentLoc).add(0.0, -1.0, 0.0),
            )
            for (adjacentLoc in adjacentLocs) {
                if (!closedList.contains(adjacentLoc) && !openList.contains(adjacentLoc) && currentLoc.block.type == Material.NETHERITE_BLOCK && currentLoc.x >= minX && currentLoc.x <= maxX && currentLoc.y >= minY && currentLoc.y <= maxY && currentLoc.z >= minZ && currentLoc.z <= maxZ) {
                    openList.add(adjacentLoc)
                }
            }
            if (currentLoc.block.type == Material.NETHERITE_BLOCK) {
                currentLoc.block.type = Material.AIR
                blockCount--
            }
        }
        return true
    }

    private fun copyLocation(loc: Location): Location {
        return Location(loc.world, loc.x, loc.y, loc.z)
    }

    private fun findNewBlock(world: World, minX: Int, maxX: Int, minY: Int, maxY: Int, minZ: Int, maxZ: Int): Location? {
        val findNewLocation = Location(world, ((minX + maxX)/2).toDouble(), minY.toDouble(), ((minZ + maxZ) / 2).toDouble())
        if (findNewLocation.block.type == Material.NETHERITE_BLOCK) {
            while (findNewLocation.block.type == Material.NETHERITE_BLOCK) {
                findNewLocation.add(0.0, 1.0, 0.0)
            }
            return findNewLocation.add(0.0, -1.0, 0.0)
        } else {
            val stopSearch: Long = System.nanoTime() + 5000000000
            for (y in minY..maxY) {
                for (x in minX..maxX) {
                    for (z in minZ..maxZ) {
                        findNewLocation.set(x.toDouble(), y.toDouble(), z.toDouble())
                        if (findNewLocation.block.type == Material.NETHERITE_BLOCK) {
                            return findNewLocation
                        }
                        if (stopSearch <= System.nanoTime()) {
                            return null
                        }
                    }
                }
            }
            return null
        }
    }
}