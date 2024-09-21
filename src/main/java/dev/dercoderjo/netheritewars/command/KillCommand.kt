package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.common.sendMessage
import net.kyori.adventure.text.Component
import org.bukkit.GameMode
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KillCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        if (sender.gameMode == GameMode.CREATIVE) {
            sendMessage(sender, Component.text("Du kannst dich nicht im Kreativmodus töten"))
            return true
        }
        if (sender.gameMode == GameMode.SPECTATOR) {
            sendMessage(sender, Component.text("Du kannst dich nicht im Zuschauermodus töten"))
            return true
        }

        sender.damage(1000.0)
        sendMessage(sender, Component.text("Du hast dich selber getötet"))
        return true
    }
}