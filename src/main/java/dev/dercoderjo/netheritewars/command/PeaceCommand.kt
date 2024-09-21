package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.message_notEnoughPermissions
import dev.dercoderjo.netheritewars.common.sendMessage
import org.bukkit.NamespacedKey
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.persistence.PersistentDataType

class PeaceCommand(private val plugin: NetheriteWars) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notEnoughPermissions(sender)
            return true
        }

        if (plugin.cachedBattleRoyalData?.status != null) {
            sendMessage(sender, "Da derzeit ein Battle Royale läuft, kannst du nicht in den Friedensmodus")
            return false
        }

        if (sender.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
            sendMessage(sender,"Du bist bereits im Friedensmodus.")
            return false
        } else {
            sender.persistentDataContainer.set(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN, true)
            sendMessage(sender, "Du bist nun im Friedensmodus.")
            return true
        }
    }
}