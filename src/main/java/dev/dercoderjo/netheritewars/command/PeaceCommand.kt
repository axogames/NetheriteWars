package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.message_notAPlayer
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
            message_notAPlayer(sender)
            return true
        }

        if (plugin.cachedBattleRoyalData?.status != null) {
            sendMessage(sender, "Da derzeit ein Battle Royale läuft, kannst du nicht in den Friedensmodus")
            return true
        }

        if (sender.persistentDataContainer.has(NamespacedKey("netheritewars", "peace"))) {
            sendMessage(sender,"Du bist bereits im Friedensmodus")
        } else {
            sender.persistentDataContainer.set(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN, true)
            sendMessage(sender, "Du bist nun im Friedensmodus")
        }
        return true
    }
}