package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import net.kyori.adventure.text.Component
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class PeaceCommand(private val plugin: NetheriteWars) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("You must be a player to use this command.")
            return false
        }

        if (plugin.cachedBattleRoyalData?.status != null) {
            sender.sendMessage(Component.text("Es läuft zurzeit ein Battle Royal."))
            return false
        }

        if (sender.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
            sender.sendMessage(Component.text("Du bist bereits im Friedensmodus."))
            return false
        } else {
            sender.persistentDataContainer.set(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN, true)
            sender.sendMessage(Component.text("Du bist nun im Friedensmodus."))
            return true
        }
    }
}