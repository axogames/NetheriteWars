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

class AddNetheriteCommand(private val plugin: NetheriteWars) : CommandExecutor, TabCompleter {
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

        val closedList: ArrayList<Location> = ArrayList()
        val openList: ArrayList<Location> = ArrayList()
        var currentLoc: Location
        var adjacentLocs: Array<Location>
        openList.add(Location(world, ((minX + maxX)/2).toDouble(), minY.toDouble(), ((minZ + maxZ) / 2).toDouble()))

        var count = Integer.valueOf(blockCount)
        while (count  > 0) {
            currentLoc = openList.removeAt(0)
            closedList.add(currentLoc)
            adjacentLocs = arrayOf(
                copyLocation(currentLoc).add(1.0, 0.0, 0.0),
                copyLocation(currentLoc).add(-1.0, 0.0, 0.0),
                copyLocation(currentLoc).add(0.0, 1.0, 0.0),
                copyLocation(currentLoc).add(0.0, -1.0, 0.0),
                copyLocation(currentLoc).add(0.0, 0.0, 1.0),
                copyLocation(currentLoc).add(0.0, 0.0, -1.0),
            )
            for (adjacentLoc in adjacentLocs) {
                if (!closedList.contains(adjacentLoc) && !openList.contains(adjacentLoc) && currentLoc.x >= minX && currentLoc.x <= maxX && currentLoc.y >= minY && currentLoc.y <= maxY && currentLoc.z >= minZ && currentLoc.z <= maxZ) {
                    openList.add(adjacentLoc)
                }
            }
            if (currentLoc.block.type == Material.AIR && currentLoc.x >= minX && currentLoc.x <= maxX && currentLoc.y >= minY && currentLoc.y <= maxY && currentLoc.z >= minZ && currentLoc.z <= maxZ) {
                currentLoc.block.type = Material.NETHERITE_BLOCK
                count--
            }
        }
        sendMessage(player, "Im VAULT von Team ${args[0]} wurden $blockCount NetheriteBlöcke platziert")
        updateTeamNetheriteInDatabase(team, 9 * blockCount, plugin)
        return true
    }

    private fun copyLocation(loc: Location): Location {
        return Location(loc.world, loc.x, loc.y, loc.z)
    }
}