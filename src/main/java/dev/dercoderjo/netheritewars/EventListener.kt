package dev.dercoderjo.netheritewars

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import dev.dercoderjo.netheritewars.common.*
import dev.dercoderjo.netheritewars.util.convertTime
import dev.dercoderjo.netheritewars.util.inBlueVault
import dev.dercoderjo.netheritewars.util.inRedVault
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.title.TitlePart
import org.bukkit.*
import org.bukkit.block.BlockState
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntitySpawnEvent
import org.bukkit.event.inventory.InventoryCreativeEvent
import org.bukkit.event.inventory.InventoryPickupItemEvent
import org.bukkit.event.player.*
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.persistence.PersistentDataType
import org.bukkit.potion.PotionEffect
import org.bukkit.potion.PotionEffectType
import kotlin.math.abs
import kotlin.math.floor


class EventListener(private val plugin: NetheriteWars) : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        checkPositionInBorders(plugin, event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        //Spieler werden vom Server gekickt, wenn sie keine Berechtigung haben, zu joinen
        if (!plugin.DATABASE.checkPlayer(event.player.uniqueId.toString()) || !plugin.DATABASE.getPlayer(event.player.uniqueId.toString()).whitelisted) {
            event.player.kick(Component.text("Du bist nicht auf der Whitelist!").color(NamedTextColor.RED).append(Component.newline()).append(Component.newline()).append(Component.text("Bitte wende dich an ein Teammitglied").color(NamedTextColor.DARK_GREEN))
            )
        }

        checkPositionInBorders(plugin, event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        //Spieler darf beim leaven maximal 16 Netherite haben, der Rest wird als Item gedroppt
        val droppingNetheriteCount = dropNetherite(event.player, false)
        if (droppingNetheriteCount < 16) {
            return
        }
        event.player.world.dropItem(event.player.location, getNetheriteItem(droppingNetheriteCount - 16)).apply {
            persistentDataContainer.set(NamespacedKey("netheritewars", "netherite_cooldown"), PersistentDataType.LONG, System.currentTimeMillis() + 60000)
            persistentDataContainer.set(NamespacedKey("netheritewars", "netherite_owner"), PersistentDataType.STRING, event.player.uniqueId.toString())
        }
    }

    @EventHandler
    fun onInventoryPickupItem(event: InventoryPickupItemEvent) {
        //Container wie Hopper können keine Netheriteingots- und Blöcke aufsammeln
        if (event.item.itemStack.type == Material.NETHERITE_INGOT || event.item.itemStack.type == Material.NETHERITE_BLOCK) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onServerTickEnd(event: ServerTickEndEvent) {
        /**
         * Der Cooldown von allen NetheriteIngots, die einen Owner haben, wird verringert.
         * Wenn ein Item keinen Colldown mehr hat, kann es on allen Spielern aufgesammelt werden.
         */
        fun reduceNetheriteItemCooldown() {
            for (world in Bukkit.getWorlds()) {
                for (entity in world.entities) {
                    if (entity.type == EntityType.ITEM) {
                        val cooldown = entity.persistentDataContainer.get(NamespacedKey("netheritewars", "netherite_cooldown"), PersistentDataType.LONG) ?: continue
                        if (cooldown < System.currentTimeMillis()) {
                            entity.persistentDataContainer.remove(NamespacedKey("netheritewars", "netherite_cooldown"))
                            entity.persistentDataContainer.remove(NamespacedKey("netheritewars", "netherite_owner"))
                            entity.customName(Component.empty())
                        } else {
                            entity.customName(
                                Component.text()
                                    .append(Component.text("Cooldown: ").color(NamedTextColor.RED))
                                    .append(
                                        Component.text(floor((cooldown - System.currentTimeMillis()).toDouble() / 1000).toInt())
                                            .color(NamedTextColor.WHITE)
                                    )
                                    .build()
                            )
                        }
                    }
                }
            }
        }

        /**
         * Ein Spieler enthält entsprechend der Anzahl an Netheriteingots und -blöcken Effekte.
         *  - 1 Level Schwäche pro 31 Netheriteingots
         *  - 1 Level Langsamkeit pro 72 Netheriteingots
         *
         *  Spieler im Kreativ- oder Zuschauermodus sind nicht betroffen.
         *
         *  @param player Der Spieler, der Effekte erhalten soll
         *  @return Ob der Spieler die effekte tatsächlich erhalten hat
         */
        fun giveEffectForMuchNetherite(player: Player) : Boolean {
            val netheriteCount = checkInventoryForNetherite(player)
            Bukkit.getScoreboardManager().mainScoreboard.getObjective("netheritewars:netherite_player")?.apply {
                getScore(player).score = netheriteCount
            }

            if (player.gameMode == GameMode.SURVIVAL || player.gameMode == GameMode.ADVENTURE) {
                val slownessStrength = (netheriteCount / 72.0).toInt()
                val weaknessStrength = (netheriteCount / 31.0).toInt()
                if (player.getPotionEffect(PotionEffectType.SLOWNESS)?.duration == -1) {
                    player.removePotionEffect(PotionEffectType.SLOWNESS)
                }
                if (player.getPotionEffect(PotionEffectType.WEAKNESS)?.duration == -1) {
                    player.removePotionEffect(PotionEffectType.WEAKNESS)
                }
                if (slownessStrength > 0) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.SLOWNESS, -1, slownessStrength - 1, false, false, true))
                }
                if (weaknessStrength > 0) {
                    player.addPotionEffect(PotionEffect(PotionEffectType.WEAKNESS, -1, weaknessStrength - 1, false, false, true))
                }
                return true
            } else {
                player.removePotionEffect(PotionEffectType.WEAKNESS)
                player.removePotionEffect(PotionEffectType.SLOWNESS)
                return false
            }
        }

        /**
         * Wenn ein Spieler ein Blockentity, dass Netherite enthält, öffnet oder geöffnet hat, so wird dieses gelöscht
         *
         * @param player Der Spieler, der überprüft werden soll
         * @return Wahrheitswert, ob der Spieler ein Blockentity, dass Netherite enthält, geöffnet hat
         */
        fun explodeBlockEntityWithNetherite(player: Player) : Boolean {
            val inventory = player.openInventory.topInventory
            for (item in inventory.contents) {
                if (item?.type == Material.NETHERITE_INGOT || item?.type == Material.NETHERITE_BLOCK) {
                    val inventoryHolder = inventory.holder
                    if (inventoryHolder is BlockState) {
                        val block = (inventoryHolder as BlockState).block
                        block.world.spawnParticle(Particle.EXPLOSION, block.location, 1)
                        for (drop in inventory.contents) {
                            if (drop != null) {
                                block.world.dropItem(block.location, drop)
                            }
                        }
                        block.type = Material.AIR
                        return true
                    } else if (inventoryHolder is Entity) {
                        val entity = inventoryHolder as Entity
                        if (entity.type == EntityType.PLAYER) {
                            continue
                        }
                        entity.remove()
                        entity.world.spawnParticle(Particle.EXPLOSION, entity.location, 1)
                        return true
                    }
                }
            }
            return false
        }

        reduceNetheriteItemCooldown()
        for (player in Bukkit.getOnlinePlayers()) {
            giveEffectForMuchNetherite(player)
            explodeBlockEntityWithNetherite(player)
        }

        if (plugin.cachedBattleRoyalData?.status == BattleRoyalStatus.STARTED || plugin.cachedBattleRoyalData?.status == BattleRoyalStatus.PAUSED) {
            val formatedTime = if (plugin.cachedBattleRoyalData?.status == BattleRoyalStatus.STARTED) {
                if (plugin.cachedBattleRoyalData?.endsAt!! - System.currentTimeMillis() <= 0) {
                    plugin.DATABASE.setBattleRoyal(BattleRoyal(BattleRoyalStatus.ENDED, plugin.cachedBattleRoyalData!!.endsAt, plugin.cachedBattleRoyalData!!.pausedAt))
                    plugin.cachedBattleRoyalData = plugin.DATABASE.getBattleRoyal()

                    Bukkit.getServer().sendTitlePart(TitlePart.TITLE, Component.text("Battle Royal beendet"))
                    return
                }
                convertTime(plugin.cachedBattleRoyalData?.endsAt!! - System.currentTimeMillis())
            } else {
                convertTime((plugin.cachedBattleRoyalData?.endsAt!! - plugin.cachedBattleRoyalData?.pausedAt!!) - System.currentTimeMillis())
            }

            Bukkit.getServer().sendActionBar(Component.text(formatedTime, NamedTextColor.GREEN))
        }
    }

    @EventHandler
    fun onPlayerPickItem(event: PlayerAttemptPickupItemEvent) {
        // Netheriteingots können nur von ihrem Owner eingesammelt werden
        val owner = event.item.persistentDataContainer.get(NamespacedKey("netheritewars", "netherite_owner"), PersistentDataType.STRING) ?: return
        if (owner != event.player.uniqueId.toString()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerBlockPlace(event: BlockPlaceEvent) {
        //Überprüft, ob ein Netheriteblock platziert werden darf
        if (event.block.type == Material.NETHERITE_BLOCK) {
            var team = Teams.UNSET
            if (inRedVault(event.block.location, plugin)) {
                team = Teams.RED
            } else if (inBlueVault(event.block.location, plugin)) {
                team = Teams.BLUE
            } else if (event.player.gameMode != GameMode.CREATIVE) {
                event.isCancelled = true
                sendMessage(event.player, "Du kannst nur in einem VAULT Netheriteblöcke platzieren")
            }
            updateTeamNetheriteInDatabase(team, 9, plugin)
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        if (plugin.cachedBattleRoyalData?.status == BattleRoyalStatus.PREPARED || plugin.cachedBattleRoyalData?.status == BattleRoyalStatus.PAUSED) {
            event.isCancelled = true
            return
        }

        if (event.entity.type ==  EntityType.PLAYER) {
            val player = event.entity as Player
            if (player.health - event.damage <= 0.0) {
                sendMessage(player, "Du bist gestorben und wirst am nächsten Morgen wiederbelebt")
                player.gameMode = GameMode.SPECTATOR
                player.persistentDataContainer.set(NamespacedKey("netheritewars", "respawn_time"), PersistentDataType.LONG, player.world.gameTime + (24000 - event.entity.world.time))

                val dbPlayer = plugin.DATABASE.getPlayer(player.uniqueId.toString())
                dbPlayer.deaths += 1
                plugin.DATABASE.setDeaths(dbPlayer)
            }
        }
    }

    @EventHandler
    fun onEntityDamageByEntity(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        val damager = event.damager

        if (!(entity.type == EntityType.PLAYER && damager.type == EntityType.PLAYER)) {
            return
        }
        val player: Player = entity as Player

        if (entity.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
            sendMessage(damager, "${entity.name} ist im Friedensmodus, weshalb du diesen Spieler nicht angreifen kannst")
            event.isCancelled = true
            return
        } else if (damager.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
            sendMessage(damager, "Du bist im Friedensmodus, weshalb du ${entity.name} nicht angreifen kannst")
            event.isCancelled = true
            return
        }

        val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")

        if ((abs(player.location.z) < borderSize || abs(damager.location.z) < borderSize) && entity.world.environment == World.Environment.NORMAL) {
            event.isCancelled = true
            sendMessage(damager, "Du kannst Spieler nicht auf der Grenze angreifen")
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type == Material.NETHERITE_BLOCK) {
            if (event.player.gameMode == GameMode.SURVIVAL) {
                event.isDropItems = false
                event.player.world.dropItem(event.block.location, getNetheriteBlock())
            }

            var team = Teams.UNSET
            if (inRedVault(event.block.location, plugin)) {
                team = Teams.RED
            } else if (inBlueVault(event.block.location, plugin)) {
                team = Teams.BLUE
            }
            updateTeamNetheriteInDatabase(team, -9, plugin)
        }
    }

    @EventHandler
    fun onLootGenerate(event: LootGenerateEvent) {
        event.setLoot(event.loot.filter { it.type != Material.NETHERITE_INGOT })
    }

    @EventHandler
    fun onEntityDamage(e: EntityDamageByEntityEvent) {
        if ((e.cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || e.cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION)
            && (e.entity.type == EntityType.ITEM)
        ) {
            val item = e.entity as Item
            val mat = item.itemStack.type
            if (mat == Material.NETHERITE_INGOT || mat == Material.NETHERITE_BLOCK || mat == Material.NETHERITE_SCRAP) {
                e.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onCreativeItemClick(event: InventoryCreativeEvent) {
        val item = event.cursor

        when (item.type) {
            Material.NETHERITE_INGOT -> event.cursor = getNetheriteItem(item.amount)
            Material.NETHERITE_BLOCK -> event.cursor = getNetheriteBlock(item.amount)
            else -> {}
        }
    }

    @EventHandler
    fun onPlayerCommand(event: PlayerCommandPreprocessEvent) {
        val player = event.player
        val command = event.message

        if (command.startsWith("/give")) {
            Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                player.inventory.contents.forEachIndexed { index, item ->
                    if (item != null && item.type != Material.AIR) {
                        when (item.type) {
                            Material.NETHERITE_INGOT -> player.inventory.setItem(index, getNetheriteItem(item.amount))
                            Material.NETHERITE_BLOCK -> player.inventory.setItem(index, getNetheriteBlock(item.amount))
                            else -> {}
                        }
                    }
                }
            }, 1L)
        }
    }

    @EventHandler
    fun onEntitySpawn(event: EntitySpawnEvent) {
        val entity = event.entity
        val entityType = entity.type

        if (entityType == EntityType.ITEM) {
            when ((entity as Item).itemStack.type) {
                Material.NETHERITE_INGOT, Material.NETHERITE_BLOCK -> entity.isGlowing = true
                else -> {}
            }
        }
    }

    @EventHandler
    fun onPlayerInteract (event: PlayerInteractEvent) {
        val player = event.player



        if (event.action == Action.RIGHT_CLICK_BLOCK && event.hand == EquipmentSlot.HAND) {
            val clickedBlock = event.clickedBlock!!
            val itemInHand = player.inventory.itemInMainHand
            if (itemInHand.type == Material.NETHERITE_INGOT && (itemInHand.amount >= 9 || player.gameMode == GameMode.CREATIVE)) {
                val blockToPlace = clickedBlock.getRelative(event.blockFace)
                if (blockToPlace.type == Material.AIR && !isPlayerInBlock(blockToPlace)) {
                        if (blockToPlace.z <= plugin.CONFIG.getInt("BORDER_SIZE") && plugin.DATABASE.getPlayer(event.player.uniqueId.toString()).team == Teams.BLUE) {
                            event.isCancelled = true
                            return
                        } else if (blockToPlace.z >= -plugin.CONFIG.getInt("BORDER_SIZE") && plugin.DATABASE.getPlayer(event.player.uniqueId.toString()).team == Teams.RED) {
                            event.isCancelled = true
                            return
                        }
                    Bukkit.getScheduler().runTaskLater(plugin, Runnable {
                        blockToPlace.type = Material.NETHERITE_BLOCK
                        val placeEvent = BlockPlaceEvent(blockToPlace, blockToPlace.state, clickedBlock, itemInHand, player, true, EquipmentSlot.HAND)
                        Bukkit.getServer().pluginManager.callEvent(placeEvent)

                        if (!placeEvent.isCancelled) {
                            if (player.gameMode != GameMode.CREATIVE) {
                                itemInHand.amount -= 9
                                sendMessage(player, "Du hast 9 NetheriteIngots aus deiner Hand in einen Block umgewandelt und platziert")
                            } else {
                                sendMessage(player, "Du hast einen Netheriteblock aus Netheriteingots platziert")
                            }
                        } else {
                            blockToPlace.type = Material.AIR
                        }
                    }, 1)

                }
            }
        }
    }
}