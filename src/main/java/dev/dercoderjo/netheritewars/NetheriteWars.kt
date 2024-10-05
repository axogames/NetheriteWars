package dev.dercoderjo.netheritewars

import dev.dercoderjo.netheritewars.command.*
import dev.dercoderjo.netheritewars.common.*
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.GameMode
import org.bukkit.GameRule
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ShapedRecipe
import org.bukkit.inventory.ShapelessRecipe
import org.bukkit.persistence.PersistentDataType
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType

class NetheriteWars : JavaPlugin() {
    val CONFIG = config
    val DATABASE = Database(this)
    var cachedBattleRoyalData: BattleRoyal? = null

    override fun onEnable() {
        Bukkit.getPluginManager().registerEvents(EventListener(this), this)

        saveDefaultConfig()

        for (world in Bukkit.getWorlds()) {
            world.setGameRule(GameRule.KEEP_INVENTORY, true)
            world.setGameRule(GameRule.SPAWN_RADIUS, 0)
        }

        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_ingot"))
        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_ingot_from_netherite_block"))
        Bukkit.removeRecipe(NamespacedKey.minecraft("netherite_block"))

        Bukkit.addRecipe(ShapelessRecipe(NamespacedKey(this, "netherite_ingot"), getNetheriteItem(9)).addIngredient(getNetheriteBlock(1)))
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
        this.getCommand("stats")?.setExecutor(StatsCommand())
        this.getCommand("help")?.setExecutor(HelpCommand(this))

        for (objective in Bukkit.getScoreboardManager().mainScoreboard.objectives) {
            objective.unregister()
        }

        Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("Blue").apply {
            color(NamedTextColor.BLUE)
            setAllowFriendlyFire(false)
            displayName(Component.text("Blau").color(NamedTextColor.BLUE))
        }
        Bukkit.getScoreboardManager().mainScoreboard.registerNewTeam("Red").apply {
            color(NamedTextColor.RED)
            setAllowFriendlyFire(false)
            displayName(Component.text("Rot").color(NamedTextColor.RED))
        }

        Bukkit.getScheduler().scheduleSyncRepeatingTask(this, {
            for (player in Bukkit.getOnlinePlayers()) {
                if (player.gameMode == GameMode.SPECTATOR && (player.location.world?.time ?: continue) <= 20 && player.persistentDataContainer.has(NamespacedKey("netheritewars", "respawn_time"))) {
                    player.gameMode = GameMode.SURVIVAL
                    player.teleport(player.respawnLocation ?: Bukkit.getWorlds()[0].spawnLocation)
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
