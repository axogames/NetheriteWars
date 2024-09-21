package dev.dercoderjo.netheritewars.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.command.CommandSender

fun sendMessage(sender: CommandSender, text: TextComponent) {
    sender.sendMessage("§x§8§8§8§8§8§8§lN§x§8§4§8§4§8§4§le§x§8§1§8§1§8§1§lt§x§7§D§7§D§7§D§lh§x§7§9§7§9§7§9§le§x§7§6§7§6§7§6§lr§x§7§2§7§2§7§2§li§x§6§F§6§F§6§F§lt§x§6§B§6§B§6§B§le§x§6§7§6§7§6§7§lW§x§6§4§6§4§6§4§la§x§6§0§6§0§6§0§lr§x§5§C§5§C§5§C§ls §x§5§5§5§5§5§5§l»§r ${text.content()}")
}

fun message_notEnoughPermissions(sender: CommandSender) {
    sendMessage(sender, Component.text("Du hast keine Berechtigung, um diesen Befehl auszuführen"))
}
fun message_notAPlayer(sender: CommandSender) {
    sendMessage(sender, Component.text("Nur Spieler können diesen Befehl ausführen"))
}

fun message_commandUsageSyntax(sender: CommandSender, commandAlias: String) {
    sendMessage(sender, Component.text("Die CommandSyntax lautet: " + when (commandAlias) {
        "addnetherite" -> "/addnetherite <Blau | Rot> <Anzahl (>0)>"
        else -> "Unbekannter alias -> $commandAlias"
    }))
}