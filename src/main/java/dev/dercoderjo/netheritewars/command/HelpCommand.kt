package dev.dercoderjo.netheritewars.command

import dev.dercoderjo.netheritewars.NetheriteWars
import dev.dercoderjo.netheritewars.common.message_notAPlayer
import dev.dercoderjo.netheritewars.common.sendMessage
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

class HelpCommand(private val plugin: NetheriteWars) : CommandExecutor {
    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            message_notAPlayer(sender)
            return true
        }

        sendMessage(sender, "/help - Ruft diese Auflistung auf")
        sendMessage(sender, "/kill - Tötet dich selber")
        sendMessage(sender, "/peace - Versetze dich in den Friedensmodus, sodass du nicht mit anderen Spielern kämpfen kannst")
        sendMessage(sender, "/nopeace - Beende den Friedensmodus für dich, sodass du wieder mit anderen Spielern kämpfen kannst")
        sendMessage(sender, "/stats - Schaltet ein Scoreboard ein bzw. aus, in dem das Netherite in den Vaults der Teams steht")

        if (plugin.DATABASE.getPlayer(sender.uniqueId.toString()).orga) {
            sendMessage(sender, "/battleroyale <prepare | start | pause | unpause | addtime <Zahl><s | m | h> | end> - Verwalte das Battle Royale Event")
            sendMessage(sender, "/gift <Zahl 1-175> [standard | blocks | random] - Platziere ein Geschenk mit <Zahl> Netheriteblöcken")
            sendMessage(sender, "/addnetherite <Blau | Rot> <Zahl >0> - Platziert <Zahl> Netheriteblöcke im Vault des ausgewählten Teams")
            sendMessage(sender, "/removenetherite <Blau | Rot> <Zahl >0> - Entfernt <Zahl> Netheriteblöcke aus dem Vault des ausgewählten Teams")
        }

        return true
    }
}