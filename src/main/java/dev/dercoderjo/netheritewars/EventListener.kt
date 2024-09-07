package dev.dercoderjo.netheritewars

import com.destroystokyo.paper.event.server.ServerTickEndEvent
import dev.dercoderjo.netheritewars.common.Teams
import dev.dercoderjo.netheritewars.util.checkInventory
import dev.dercoderjo.netheritewars.util.checkPosition
import dev.dercoderjo.netheritewars.util.spawnNetheriteBlockEntity
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.*
import org.bukkit.block.BlockState
import org.bukkit.entity.Entity
import org.bukkit.entity.EntityType
import org.bukkit.entity.LivingEntity
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.inventory.*
import org.bukkit.event.player.PlayerAttemptPickupItemEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.event.player.PlayerQuitEvent
import org.bukkit.event.world.LootGenerateEvent
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import kotlin.math.abs
import kotlin.math.floor


class EventListener(private val plugin: NetheriteWars) : Listener {
    @EventHandler
    fun onPlayerMove(event: PlayerMoveEvent) {
        checkPosition(plugin, event.player)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        if (!plugin.DATABASE.checkPlayer(event.player.uniqueId.toString()) || !plugin.DATABASE.getPlayer(event.player.uniqueId.toString()).whitelisted) {
            event.player.kick(Component.text("Du bist nicht auf der Whitelist!").color(NamedTextColor.RED)
                .append(Component.newline())
                .append(Component.newline())
                .append(Component.text("Bitte wende dich an ein Teammitglied").color(NamedTextColor.DARK_GREEN))
            )
        }

        checkPosition(plugin, event.player)
    }

    @EventHandler
    fun onPlayerQuit(event: PlayerQuitEvent) {
        var netheriteCount = 0

        for (item in event.player.inventory.contents) {
            if (item?.type == Material.NETHERITE_INGOT) {
                netheriteCount += item.amount
            } else if (item?.type == Material.NETHERITE_BLOCK) {
                netheriteCount += item.amount * 9
            }
        }

        if (netheriteCount > 16) {
            var droppingNetheriteCount = netheriteCount

            for (item in event.player.inventory.contents) {
                if (item?.type == Material.NETHERITE_INGOT) {
                    netheriteCount -= item.amount
                    item.amount = 0
                }

                if (netheriteCount <= 16) {
                    break
                }

                if (item?.type == Material.NETHERITE_BLOCK) {
                    netheriteCount -= item.amount * 9
                    item.amount = 0
                }

                if (netheriteCount <= 16) {
                    break
                }
            }

            if (netheriteCount < 16) {
                event.player.inventory.addItem(ItemStack(Material.NETHERITE_INGOT, 16 - netheriteCount))
            }

            event.player.world.dropItem(event.player.location, ItemStack(Material.NETHERITE_INGOT, droppingNetheriteCount - 16).apply {
                val meta = itemMeta
                meta?.setEnchantmentGlintOverride(true)
                itemMeta = meta
            }).apply {
                isCustomNameVisible = true
                isGlowing = true
                persistentDataContainer.set(NamespacedKey("netheritewars", "netherite_cooldown"), PersistentDataType.LONG, System.currentTimeMillis() + 60000)
                persistentDataContainer.set(NamespacedKey("netheritewars", "netherite_owner"), PersistentDataType.STRING, event.player.uniqueId.toString())
            }
        }
    }

    @EventHandler
    fun onInventoryPickupItem(event: InventoryPickupItemEvent) {
        if (event.item.itemStack.type == Material.NETHERITE_INGOT || event.item.itemStack.type == Material.NETHERITE_BLOCK) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onServerTickEnd(event: ServerTickEndEvent) {
        for (world in Bukkit.getWorlds()) {
            for (entity in world.entities) {
                if (entity.type == EntityType.ITEM) {
                    val cooldown = entity.persistentDataContainer.get(NamespacedKey("netheritewars", "netherite_cooldown"), PersistentDataType.LONG) ?: continue

                    if (cooldown < System.currentTimeMillis()) {
                        entity.persistentDataContainer.remove(NamespacedKey("netheritewars", "netherite_cooldown"))
                        entity.persistentDataContainer.remove(NamespacedKey("netheritewars", "netherite_owner"))
                        entity.customName(Component.empty())
                    } else {
                        entity.customName(Component.text()
                            .append(Component.text("Cooldown: ").color(NamedTextColor.RED))
                            .append(Component.text(floor((cooldown - System.currentTimeMillis()).toDouble() / 1000).toInt()).color(NamedTextColor.WHITE))
                            .build()
                        )
                    }
                }
            }
        }

        Bukkit.getScoreboardManager().mainScoreboard.getObjective("netheritewars:netherite_player")?.apply {
            for (player in Bukkit.getOnlinePlayers()) {
                getScore(player).score = checkInventory(player)

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
                        } else if (inventoryHolder is Entity) {
                            val entity = inventoryHolder as Entity
                            if (entity.type == EntityType.PLAYER) {
                                continue
                            }
                            entity.remove()
                            entity.world.spawnParticle(Particle.EXPLOSION, entity.location, 1)
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    fun onPlayerPickItem(event: PlayerAttemptPickupItemEvent) {
        val owner = event.item.persistentDataContainer.get(NamespacedKey("netheritewars", "netherite_owner"), PersistentDataType.STRING) ?: return

        if (owner != event.player.uniqueId.toString()) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onPlayerPlaceBlock(event: BlockPlaceEvent) {
        if (event.itemInHand.type == Material.NETHERITE_BLOCK && event.block.world.getBlockAt(event.block.location.x.toInt(), 70, event.block.location.z.toInt()).type != Material.BEDROCK) {
            event.isCancelled = true
        }
    }

    @EventHandler
    fun onEntityDamage(event: EntityDamageEvent) {
        val borderSize = plugin.CONFIG.getInt("BORDER_SIZE")
        val causingEntityLocationZ = event.damageSource.causingEntity?.location?.z ?: (borderSize + 1.0)

        if (event.damageSource.causingEntity is Player) {
            if ((abs(event.entity.location.z) < borderSize || abs(causingEntityLocationZ) < borderSize || event.entity.world.environment != World.Environment.NORMAL) && event.entity.type == EntityType.PLAYER) {
                event.isCancelled = true

                (event.damageSource.causingEntity as Player).sendMessage(Component.text("Du kannst Spieler nicht auf der Grenze töten!").color(NamedTextColor.RED))
            }

            if (event.damageSource.causingEntity?.persistentDataContainer?.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
                event.isCancelled = true
            }
        }

        if (event.entity is Player) {
            if ((event.entity as Player).health - event.damage <= 0.0) {
                (event.entity as Player).gameMode = GameMode.SPECTATOR
                event.entity.persistentDataContainer.set(NamespacedKey("netheritewars", "respawn_time"), PersistentDataType.LONG, event.entity.world.gameTime + (24000 - event.entity.world.time))
            }

            if (event.entity.persistentDataContainer.get(NamespacedKey("netheritewars", "peace"), PersistentDataType.BOOLEAN) == true) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        if (event.block.type == Material.NETHERITE_BLOCK) {
            event.isDropItems = false
            spawnNetheriteBlockEntity(event.block.location.add(0.5, 0.5, 0.5))

            val team = plugin.DATABASE.getPlayer(event.player.uniqueId.toString()).team

            if (team == Teams.BLUE) {
                plugin.DATABASE.setTeam(plugin.DATABASE.getTeam(Teams.BLUE).apply {
                    netherite = plugin.DATABASE.getTeam(Teams.BLUE).netherite - 9
                })
            } else if (team == Teams.RED) {
                plugin.DATABASE.setTeam(plugin.DATABASE.getTeam(Teams.RED).apply {
                    netherite = plugin.DATABASE.getTeam(Teams.RED).netherite - 9
                })
            }
        }
    }

    @EventHandler
    fun onLootGenerate(event: LootGenerateEvent) {
        event.setLoot(event.loot.filter { it.type != Material.NETHERITE_INGOT })
    }
}
