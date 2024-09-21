package dev.dercoderjo.netheritewars.common

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.TextComponent
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender

fun sendMessage(sender: CommandSender, text: TextComponent) {
    sender.sendMessage("§x§8§8§8§8§8§8§lN§x§8§4§8§4§8§4§le§x§8§1§8§1§8§1§lt§x§7§D§7§D§7§D§lh§x§7§9§7§9§7§9§le§x§7§6§7§6§7§6§lr§x§7§2§7§2§7§2§li§x§6§F§6§F§6§F§lt§x§6§B§6§B§6§B§le§x§6§7§6§7§6§7§lW§x§6§4§6§4§6§4§la§x§6§0§6§0§6§0§lr§x§5§C§5§C§5§C§ls §x§5§5§5§5§5§5§l»§r ${text.content()}")
}

fun sendMessage(sender: CommandSender, text: String) {
    sendMessage(sender, Component.text(text))
}

fun announceMessage(text: TextComponent) {
    for (player in Bukkit.getOnlinePlayers()) {
        sendMessage(player, text)
    }
}

fun announceMessage(text: String) {
    announceMessage(Component.text(text))
}

fun message_notEnoughPermissions(sender: CommandSender) {
    sendMessage(sender, "Du hast keine Berechtigung, um diesen Befehl auszuführen")
}
fun message_notAPlayer(sender: CommandSender) {
    sendMessage(sender, "Nur Spieler können diesen Befehl ausführen")
}

fun message_commandUsageSyntax(sender: CommandSender, commandAlias: String) {
    sendMessage(sender, "Die CommandSyntax lautet: " + when (commandAlias) {
        "addnetherite" -> "/addnetherite <Blau | Rot> <Zahl >0>"
        "battleroyale" -> "/battleroyale <prepare | start | pause | unpause | addtime <Zahl><s | m | h> | end>"
        "gift" -> "/gift <Zahl 1-175> [standard | blocks | random]"
        "removenetherite", "remnetherite" -> "/removenetherite <Blau | Rot> <Zahl >0>"
        else -> "Unbekannter alias -> $commandAlias"
    })
}

fun message_commandUsageSyntax(sender: CommandSender, commandAlias: String, reason: TextComponent) {
    sendMessage(sender, reason)
    message_commandUsageSyntax(sender, commandAlias)
}

fun message_commandUsageSyntax(sender: CommandSender, commandAlias: String, reason: String) {
    message_commandUsageSyntax(sender, commandAlias, Component.text(reason))
}