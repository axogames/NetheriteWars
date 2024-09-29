package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.*
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
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        if (args.size == 1) {
            return mutableListOf("Blau","Rot")
        }
        return mutableListOf()
    }

    override fun onCommand(sender: CommandSender, command: Command, alias: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notAPlayer(sender)
            return true
        }
        val player: Player = sender
        if (!plugin.DATABASE.getPlayer(player.uniqueId.toString()).orga) {
            message_notEnoughPermissions(player)
            return true
        }

        if (args.size != 2) {
            message_commandUsageSyntax(player, alias, "Du musst genau zwei Argumente angeben")
            return true
        }

        val minX: Int
        val maxX: Int
        val minZ: Int
        val maxZ: Int
        val team: Teams
        if (args[0] == "Blau") {
            team = Teams.BLUE
            minX = plugin.CONFIG.getInt("VAULT.BLUE.MINX")
            maxX = plugin.CONFIG.getInt("VAULT.BLUE.MAXX")
            minZ = plugin.CONFIG.getInt("VAULT.BLUE.MINZ")
            maxZ = plugin.CONFIG.getInt("VAULT.BLUE.MAXZ")
        } else if (args[0] == "Rot") {
            team = Teams.RED
            minX = plugin.CONFIG.getInt("VAULT.RED.MINX")
            maxX = plugin.CONFIG.getInt("VAULT.RED.MAXX")
            minZ = plugin.CONFIG.getInt("VAULT.RED.MINZ")
            maxZ = plugin.CONFIG.getInt("VAULT.RED.MAXZ")
        } else {
            message_commandUsageSyntax(sender, alias, "Du musst ein gültiges Team angeben")
            return true
        }
        val minY: Int = plugin.CONFIG.getInt("VAULT.MINY")
        val maxY: Int = plugin.config.getInt("VAULT.MAXY")

        val blockCount: Int
        try {
            blockCount = args[1].toInt()
        } catch (exception: Exception) {
            message_commandUsageSyntax(sender, alias, "${args[1]} ist keine Zahl")
            return true
        }
        if (blockCount < 1) {
            message_commandUsageSyntax(sender, alias, "Die Blockanzahl muss größer als 0 sein")
            return true
        }

        val world: World = if (Bukkit.getWorld("world") == null) {
            Bukkit.getWorlds()[0]
        } else {
            Bukkit.getWorld("world")!!
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

        var count = Integer.valueOf(blockCount)
        while (count > 0) {
            if (openList.isEmpty()) {
                val newLocation: Location ?= findNewBlock(world, minX, maxX, minY, maxY, minZ, maxZ)
                if (newLocation == null) {
                    if (count > 1) {
                        sendMessage(player, "Es konnten $count NetheriteBlöcke nicht entfernt werden.")
                    } else {
                        sendMessage(player, "Es konnte 1 Netheriteblock nicht entfernt werden")
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
                count--
            }
        }
        sendMessage(player, "Im VAULT von Team ${args[0]} wurden $blockCount NetheriteBlöcke entfernt")
        updateTeamNetheriteInDatabase(team, -9 * blockCount, plugin)
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