package dev.dercoderjo.netheritewars.command

import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class NoPeaceCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (sender.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == false) {
            sender.sendMessage(Component.text("Du bist bereits im Kriegsmodus."))
            return false
        } else {
            sender.persistentDataContainer.remove(NamespacedKey("netheritewars", "peace"))
            sender.sendMessage(Component.text("Du bist nun im Kriegsmodus."))
            return true
        }
    }
}