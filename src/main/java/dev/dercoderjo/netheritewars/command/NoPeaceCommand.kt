package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.common.message_notAPlayer
import dev.dercoderjo.netheritewars.common.sendMessage
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class NoPeaceCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notAPlayer(sender)
            return true
        }

        if (sender.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == false) {
            sendMessage(sender, "Du bist bereits im Kriegsmodus.")
        } else {
            sender.persistentDataContainer.remove(NamespacedKey("netheritewars", "peace"))
            sendMessage(sender, "Du bist nun im Kriegsmodus.")
        }
        return true
    }
}