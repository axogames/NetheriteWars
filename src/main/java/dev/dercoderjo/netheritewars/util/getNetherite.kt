package dev.dercoderjo.netheritewars.util

import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.TextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Material
import org.bukkit.inventory.ItemStack

fun getNetheriteItem(amount: Int = 1): ItemStack {
    return ItemStack(Material.NETHERITE_INGOT, amount).apply {
        itemMeta = itemMeta?.apply {
            setEnchantmentGlintOverride(true)
            displayName(Component.text("Lupenreiner Netheritbarren").color(TextColor.fromHexString("#4A4A4A")).decoration(TextDecoration.ITALIC, false))
        }
    }
}

fun getNetheriteBlock(amount: Int = 1): ItemStack {
    return ItemStack(Material.NETHERITE_BLOCK, amount).apply {
        itemMeta = itemMeta?.apply {
            setEnchantmentGlintOverride(true)
            displayName(Component.text("Lupenreiner Netheritblock").color(TextColor.fromHexString("#4A4A4A")).decoration(TextDecoration.ITALIC, false)
            )
        }
    }
}