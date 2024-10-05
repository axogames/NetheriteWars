package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.common.message_notAPlayer
import dev.dercoderjo.netheritewars.common.sendMessage
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class StatsCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notAPlayer(sender)
            return true
        }

        if (sender.persistentDataContainer.has(NamespacedKey("netheritewars", "stats"))) {
            sender.persistentDataContainer.remove(NamespacedKey("netheritewars", "stats"))
            sendMessage(sender,"Du hast die Teamstatistiken ausgeblendet")
        } else {
            sender.persistentDataContainer.set(NamespacedKey("netheritewars", "stats"), PersistentDataType.BOOLEAN, true)
            sendMessage(sender, "Du hast die Teamstatistiken eingeblendet")
        }
        return true
    }
}