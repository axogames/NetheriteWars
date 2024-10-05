package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.common.message_notAPlayer
import dev.dercoderjo.netheritewars.common.sendMessage
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class NoPeaceCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notAPlayer(sender)
            return true
        }

        if (sender.persistentDataContainer.has(NamespacedKey("netheritewars", "peace"))) {
            sender.persistentDataContainer.remove(NamespacedKey("netheritewars", "peace"))
            sendMessage(sender, "Du bist nun nicht mehr im Friedensmodus")
        } else {
            sendMessage(sender, "Du bist nicht im Friedensmodus")
        }
        return true
    }
}