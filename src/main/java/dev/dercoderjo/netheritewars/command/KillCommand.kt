package dev.dercoderjo.netheritewars.command

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class KillCommand : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            return false
        }

        sender.damage(1000.0)

        return true
    }
}