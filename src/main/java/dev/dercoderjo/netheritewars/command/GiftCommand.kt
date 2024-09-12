package or.NOahhh_xd.testPlugin

import dev.dercoderjo.netheritewars.NetheriteWars
import net.kyori.adventure.text.Component
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.util.Vector
import java.util.*

class GiftCommand(private val plugin: NetheriteWars) : CommandExecutor {
    private val chest: Array<BlockWithPos> = arrayOf(
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 6.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-4.0, 6.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-4.0, 6.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-4.0, 6.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 6.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 7.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-4.0, 7.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-4.0, 7.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-4.0, 7.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 7.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-4.0, 7.0, 3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-4.0, 8.0, -3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-4.0, 8.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 0.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 1.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 1.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 1.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 1.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 1.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 1.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 1.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 2.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 2.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 2.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 2.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 2.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 2.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 2.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 3.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 3.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 3.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 3.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 3.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 3.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 3.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 4.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 4.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 4.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 4.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 4.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 4.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 4.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 5.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 5.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 5.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 5.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 5.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 5.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 5.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 6.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 6.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 6.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 6.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 7.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-3.0, 7.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-3.0, 7.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-3.0, 7.0, 4.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-3.0, 8.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-3.0, 8.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-3.0, 9.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-3.0, 9.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 0.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 1.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 1.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 2.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 2.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 3.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 3.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 4.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 4.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 5.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 5.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 6.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 6.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 7.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 7.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 7.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 8.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-2.0, 8.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-2.0, 8.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-2.0, 8.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-2.0, 8.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-2.0, 9.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-2.0, 9.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(-1.0, 0.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 1.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 1.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 2.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 2.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 3.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 3.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 4.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 4.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 5.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 5.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 6.0, -4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 6.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 6.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 6.0, 4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 7.0, -4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 7.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 7.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 7.0, 4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 8.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 8.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(-1.0, 8.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 8.0, 1.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(-1.0, 8.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-1.0, 9.0, -1.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-1.0, 9.0, 0.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(-1.0, 9.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(0.0, 0.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 1.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 1.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 2.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 2.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 3.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 3.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 4.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 4.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 5.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 5.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 6.0, -4.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 6.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 6.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 6.0, 4.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 7.0, -4.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 7.0, -3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 7.0, 3.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 7.0, 4.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 8.0, -2.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 8.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 8.0, 0.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 8.0, 1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(0.0, 8.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 9.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 9.0, -1.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 9.0, 0.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 9.0, 1.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 9.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 10.0, -3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 10.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 10.0, 0.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 10.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 10.0, 3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 11.0, -3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 11.0, 0.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 11.0, 3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, -3.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, -2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, -1.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, 0.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, 1.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, 2.0)),
        BlockWithPos(Material.LIME_TERRACOTTA, Vector(0.0, 12.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(1.0, 0.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 1.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 1.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 2.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 2.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 3.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 3.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 4.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 4.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 5.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 5.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 6.0, -4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 6.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 6.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 6.0, 4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 7.0, -4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 7.0, -3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 7.0, 3.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 7.0, 4.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 8.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 8.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(1.0, 8.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 8.0, 1.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(1.0, 8.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 0.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 1.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 1.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 2.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 2.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 3.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 3.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 4.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 4.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 5.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 5.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 6.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 6.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 7.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 7.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 7.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 8.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(2.0, 8.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(2.0, 8.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(2.0, 8.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(2.0, 8.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, -2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, -1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, 0.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 0.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 1.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 1.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 1.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 1.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 1.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 1.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 1.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 2.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 2.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 2.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 2.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 2.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 2.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 2.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 3.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 3.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 3.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 3.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 3.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 3.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 3.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 4.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 4.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 4.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 4.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 4.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 4.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 4.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 5.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 5.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 5.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 5.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 5.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 5.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 5.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 6.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 6.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 6.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 6.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, -4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 7.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(3.0, 7.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(3.0, 7.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(3.0, 7.0, 4.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 6.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 6.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(4.0, 6.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(4.0, 6.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(4.0, 6.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 6.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 6.0, 3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 7.0, -3.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 7.0, -2.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(4.0, 7.0, -1.0)),
        BlockWithPos(Material.GREEN_WOOL, Vector(4.0, 7.0, 0.0)),
        BlockWithPos(Material.LIME_WOOL, Vector(4.0, 7.0, 1.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 7.0, 2.0)),
        BlockWithPos(Material.RED_WOOL, Vector(4.0, 7.0, 3.0))
    )

    inner class BlockWithPos(var mat: Material, var relativePos: Vector) {
        override fun toString(): String {
            return "new BlockWithPos(Material.$mat,new Vector($relativePos))"
        }
    }

    override fun onCommand(sender: CommandSender, command: Command, s: String, args: Array<String>): Boolean {
        if (sender !is Player) return false

        val p = sender
        if (!plugin.DATABASE.getPlayer(p.uniqueId.toString()).orga) {
            p.sendMessage(Component.text("Das darfst du nicht machen!"))
            return false
        }
        val world = p.world
        if (args.isEmpty()) {
            sender.sendMessage(Component.text("/chest <amount_of_netherite_blocks>"))
        }

        val num: Int
        try {
            num = args[0].toInt()
        } catch (e: NumberFormatException) {
            p.sendMessage(Component.text(args[0] + " ist keine wirkliche Zahl"))
            return false
        }
        val pLoc = p.location
        for (b in chest) {
            copyLocation(pLoc).add(b.relativePos).block.type = b.mat
        }

        if (args.size == 1 || args.size == 2 && args[1] == "random") {
            setNetheriteBlocksRandom(copyLocation(pLoc).add(0.0, 1.0, 0.0), num)
        } else if (args.size == 2) {
            if (args[1] == "standard")
                setNetheriteBlocks(copyLocation(pLoc).add(0.0, 1.0, 0.0), num)
            else if (args[1] == "blocks") {
                setNetheriteBlocksInBlocks(copyLocation(pLoc).add(0.0, 1.0, 0.0), num)
            }
        }

        return true
    }

    private fun setNetheriteBlocksRandom(loc: Location, num: Int) {
        var num = num
        val fields = IntArray(25)
        val rand = Random()
        while (num > 0) {
            val i = rand.nextInt(25)
            val x = i / 5 - 2
            val z = i % 5 - 2
            copyLocation(loc).add(x.toDouble(), fields[i]++.toDouble(), z.toDouble()).block.type =
                Material.NETHERITE_BLOCK
            num--
        }
    }

    private fun setNetheriteBlocksInBlocks(loc: Location, num: Int) {
        var num = num
        val fields = IntArray(25)
        val rand = Random()
        val lastPos = rand.nextInt(25)
        fields[lastPos] = 1
        num--
        while (num > 0) {
            if (rand.nextInt(10) > 6) {
                val i = rand.nextInt(4)
                var x = 0
                var z = 0
                when (i) {
                    0 -> {
                        if ((lastPos + 1) / 5 == lastPos / 5) {
                            x = 1
                        }
                    }

                    1 -> {
                        if ((lastPos - 1) / 5 == lastPos / 5) {
                            x = -1
                        }
                    }

                    2 -> {
                        if (lastPos + 5 < fields.size) {
                            z = 1
                        }
                    }

                    3 -> {
                        if (lastPos - 5 >= 0) {
                            z = -1
                        }
                    }
                }
                copyLocation(loc).add(x.toDouble(), fields[i]++.toDouble(), z.toDouble()).block.type =
                    Material.NETHERITE_BLOCK
            }
            copyLocation(loc).add(
                (lastPos / 5 - 2).toDouble(),
                fields[lastPos]++.toDouble(),
                (lastPos % 5 - 2).toDouble()
            ).block.type =
                Material.NETHERITE_BLOCK
            num--
        }
    }

    private fun setNetheriteBlocks(pLoc: Location, count: Int) {
        var count = count
        val height = count / 25
        for (j in 0..height) {
            for (i in -2..2) {
                for (k in -2..2) {
                    val loc = copyLocation(pLoc).add(i.toDouble(), j.toDouble(), k.toDouble())
                    loc.block.type = Material.NETHERITE_BLOCK
                    count--
                    if (count <= 0) {
                        return
                    }
                }
            }
        }
    }

    fun copyLocation(loc: Location): Location {
        return Location(loc.world, loc.x, loc.y, loc.z)
    }
}