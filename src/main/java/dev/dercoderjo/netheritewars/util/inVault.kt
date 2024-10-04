package dev.dercoderjo.netheritewars.util

import dev.dercoderjo.netheritewars.NetheriteWars
import org.bukkit.Location

fun inBlueVault(location: Location, plugin: NetheriteWars) : Boolean {
    val locX = location.blockX
    val locY = location.blockY
    val locZ = location.blockZ

    return locX >= plugin.CONFIG.getInt("VAULT.BLUE.MINX") && locX <= plugin.CONFIG.getInt("VAULT.BLUE.MAXX") && locZ >= plugin.CONFIG.getInt("VAULT.BLUE.MINZ") && locZ <= plugin.CONFIG.getInt("VAULT.BLUE.MAXZ") && locY >= plugin.CONFIG.getInt("VAULT.MINY") && locY <= plugin.CONFIG.getInt("VAULT.MAXY")
}

fun inRedVault(location: Location, plugin: NetheriteWars) : Boolean {
    val locX = location.blockX
    val locY = location.blockY
    val locZ = location.blockZ

    return locX >= plugin.CONFIG.getInt("VAULT.RED.MINX") && locX <= plugin.CONFIG.getInt("VAULT.RED.MAXX") && locZ >= plugin.CONFIG.getInt("VAULT.RED.MINZ") && locZ <= plugin.CONFIG.getInt("VAULT.RED.MAXZ") && locY >= plugin.CONFIG.getInt("VAULT.MINY") && locY <= plugin.CONFIG.getInt("VAULT.MAXY")
}

fun inAnyVault(location: Location, plugin: NetheriteWars) : Boolean {
    val locX = location.blockX
    val locY = location.blockY
    val locZ = location.blockZ

    return ((locX >= plugin.CONFIG.getInt("VAULT.RED.MINX") && locX <= plugin.CONFIG.getInt("VAULT.RED.MAXX") && locZ >= plugin.CONFIG.getInt("VAULT.RED.MINZ") && locZ <= plugin.CONFIG.getInt("VAULT.RED.MAXZ")) || (locX >= plugin.CONFIG.getInt("VAULT.BLUE.MINX") && locX <= plugin.CONFIG.getInt("VAULT.BLUE.MAXX") && locZ >= plugin.CONFIG.getInt("VAULT.BLUE.MINZ") && locZ <= plugin.CONFIG.getInt("VAULT.BLUE.MAXZ"))) && locY >= plugin.CONFIG.getInt("VAULT.MINY") && locY <= plugin.CONFIG.getInt("VAULT.MAXY")

}