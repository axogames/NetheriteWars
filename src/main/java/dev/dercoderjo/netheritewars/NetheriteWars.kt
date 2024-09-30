package dev.dercoderjo.netheritewars

import dev.dercoderjo.netheritewars.command.*
import dev.dercoderjo.netheritewars.common.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import org.bukkit.scoreboard.Criteria
import org.bukkit.scoreboard.DisplaySlot

class NetheriteWars : JavaPlugin() {
    val CONFIG = config
    val DATABASE = Database(this)
    var cachedBattleRoyalData: BattleRoyal? = null

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(EventListener(this), this)

        saveDefaultConfig()


        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_ingot"))
        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_ingot_from_netherite_block"))
        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_block"))

        Bukkit.addRecipe(ShapelessRecipe(NamespacedKey(this, "netherite_ingot"), getNetheriteItem(9)).addIngredient(
            getNetheriteBlock(1)))
        Bukkit.addRecipe(ShapedRecipe(NamespacedKey(this, "netherite_block"), getNetheriteBlock(1)).shape("NNN","NNN","NNN").setIngredient('N', getNetheriteItem(9)))

        this.getCommand("kill")?.setExecutor(KillCommand())
        this.getCommand("peace")?.setExecutor(PeaceCommand(this))
        this.getCommand("nopeace")?.setExecutor(NoPeaceCommand())
        this.getCommand("gift")?.setExecutor(GiftCommand(this))
        this.getCommand("battleroyale")?.setExecutor(BattleRoyalCommand(this))
        this.getCommand("battleroyale")?.tabCompleter = BattleRoyalCommand(this)
        this.getCommand("addnetherite")?.setExecutor(AddNetheriteCommand(this))
        this.getCommand("addnetherite")?.tabCompleter = AddNetheriteCommand(this)
        this.getCommand("removenetherite")?.setExecutor(RemoveNetheriteCommand(this))
        this.getCommand("removenetherite")?.tabCompleter = RemoveNetheriteCommand(this)


        Bukkit.getScoreboardManager().mainScoreboard.getObjective("netheritewars:netherite_player")?.apply { displaySlot = DisplaySlot.PLAYER_LIST } ?: Bukkit.getScoreboardManager().mainScoreboard.registerNewObjective("netheritewars:netherite_player", Criteria.DUMMY, Component.empty()).apply { displaySlot = DisplaySlot.PLAYER_LIST }
        if (Bukkit.getScoreboardManager().mainScoreboard.getTeam("red") == null) {
            Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("red").apply {
                prefix(Component.text("[Rot]").color(NamedTextColor.RED))
            }
        }
        if (Bukkit.getScoreboardManager().mainScoreboard.getTeam("blue") == null) {
            Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("blue").apply {
                prefix(Component.text("[Blau]").color(NamedTextColor.BLUE))
            }
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.gameMode == GameMode.SPECTATOR && (player.location.world?.time ?: continue) <= 20 && player.persistentDataContainer.has(NamespacedKey("netheritewars", "respawn_time"))) {
                    player.gameMode = GameMode.SURVIVAL
                    player.teleport(player.respawnLocation ?: player.player!!.world.spawnLocation)
                    player.persistentDataContainer.remove(NamespacedKey("netheritewars", "respawn_time"))
                }

                if (player.persistentDataContainer.has(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN)) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.GLOWING, 30, 0, false, false, false))
                    player.sendActionBar(Component.text("Friedensmodus aktiviert", NamedTextColor.GREEN))
                }

                DATABASE.setPlayer(DATABASE.getPlayer(player.uniqueId.toString()).apply {
                    netherite = checkInventoryForNetherite(player)
                    }
                )
            }
        }, 0, 20)

        cachedBattleRoyalData = DATABASE.getBattleRoyal()
    }

    override fun onDisable() {
        DATABASE.disconnect()
    }
}
