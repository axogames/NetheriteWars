package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.BattleRoyal
import dev.dercoderjo.netheritewars.common.BattleRoyalStatus
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.persistence.PersistentDataType

class BattleRoyalCommand(private val plugin: NetheriteWars) : CommandExecutor, TabExecutor {
    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): MutableList<String> {
        return if (args[0] == "addtime") {
            mutableListOf(args[1] + "s", args[1] + "m", args[1] + "h")
        } else {
            if (args.size != 1) {
                mutableListOf("prepare", "start", "pause", "unpause", "addtime", "end")
            } else {
                mutableListOf("prepare", "start", "pause", "unpause", "addtime", "end").filter { it.startsWith(args[0]) }.toMutableList()
            }
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        val battleRoyal = plugin.DATABASE.getBattleRoyal()
        val currentStatus = battleRoyal.status

        return if (args[0] == "prepare" && currentStatus != BattleRoyalStatus.PREPARED) {
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.PREPARED, null, null))
            sender.sendMessage("Battle Royal prepared")

            Bukkit.getWorld("world")?.worldBorder?.setSize(48.0, 0)
            for (player in Bukkit.getOnlinePlayers()) {
                player.teleport(Location(player.world, 0.0, 100.0, 0.0))
                if (player.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
                    player.persistentDataContainer.remove(NamespacedKey("netheritewars", "peace"))
                    player.sendMessage(Component.text("Du bist nun im Kriegsmodus."))
                }
            }
            true
        } else if (args[0] == "start" && currentStatus == BattleRoyalStatus.PREPARED) {
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.STARTED, System.currentTimeMillis() + 360000000, null))
            sender.sendMessage("Battle Royal started")

            plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()
            true
        } else if (args[0] == "pause" && currentStatus == BattleRoyalStatus.STARTED) {
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.PAUSED, battleRoyal.endsAt, System.currentTimeMillis()))
            sender.sendMessage("Battle Royal paused")

            plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()
            true
        } else if (args[0] == "unpause" && currentStatus == BattleRoyalStatus.PAUSED) {
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.STARTED, System.currentTimeMillis() + (battleRoyal.endsAt!! - battleRoyal.pausedAt!!), null))
            sender.sendMessage("Battle Royal unpaused")

            plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()
            true
        } else if (args[0] == "addtime" && currentStatus == BattleRoyalStatus.STARTED) {
            val time = args[1].substring(0, args[1].length - 1).toLong()
            val newTime = when (args[1].substring(args[1].length - 1)) {
                "s" -> time * 1000
                "m" -> time * 60000
                "h" -> time * 360000000
                else -> time
            }
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.STARTED, battleRoyal.endsAt!! + newTime, battleRoyal.pausedAt))
            sender.sendMessage("Time added")

            plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()
            true
        } else if (args[0] == "end" && currentStatus != BattleRoyalStatus.ENDED) {
            plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.ENDED, battleRoyal.endsAt, null))
            sender.sendMessage("Battle Royal ended")

            plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()
            true
        } else {
            false
        }
    }
}