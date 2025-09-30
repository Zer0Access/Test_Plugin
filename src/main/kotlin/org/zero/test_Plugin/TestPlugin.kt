package org.zero.test_Plugin

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.papermc.paper.datacomponent.item.DyedItemColor
import io.papermc.paper.registry.keys.ItemTypeKeys
import net.kyori.adventure.text.format.NamedTextColor
import net.kyori.adventure.text.format.TextDecoration
import org.bukkit.Color
import org.bukkit.Sound
import org.bukkit.attribute.Attribute
import org.bukkit.attribute.AttributeModifier
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDeathEvent
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerMoveEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.CraftingRecipe
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.Recipe
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import kotlin.concurrent.thread


class TestPlugin : JavaPlugin(), Listener {

    override fun onEnable() {
        // Plugin startup logic
        server.pluginManager.registerEvents(this, this)
    }

    @EventHandler
    fun onPlayerJoin(event: PlayerJoinEvent) {
        val player = event.player
        player.sendMessage("Welcome to the server, ${player.name}!")
    }

    @EventHandler
    fun onEntityDeath(event: EntityDeathEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            player.playSound(player.location, Sound.ITEM_MACE_SMASH_GROUND_HEAVY, 1f, 1f)
        }
    }

    @EventHandler
    fun onUseTotem(event: EntityResurrectEvent) {
        if (event.entity is Player) {
            val player: Player = event.entity as Player
            player.sendMessage ("Your totem of undying has activated!")
            player.walkSpeed = 0.4f // Doubles walk speed
            thread(start = true) {
                Thread.sleep(5000) // Wait for 5 seconds
                player.walkSpeed = 0.2f // Resets walk speed to normal
                player.sendMessage ("Your spell of speed has expired.")
            }
        }
    }

    @EventHandler
    fun onUseStrengthSword(event: PlayerInteractEvent) {
        val player = event.player
        val cooldownKey = "SwordCooldown"
        val cooldown = player.getMetadata(cooldownKey).firstOrNull()?.asLong() ?: 0L
        val now = System.currentTimeMillis()
        // Only apply effect when right-clicking (use), not left-clicking (attack)
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.item?.type?.name == "NETHERITE_SWORD" &&
            event.item?.itemMeta?.let { meta ->
                meta.hasItemName() && meta.itemName() == net.kyori.adventure.text.Component.text()
                    .content("Sword of Strength")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
            if (now < cooldown) {
                player.sendActionBar(net.kyori.adventure.text.Component
                    .text("You must wait before using the ability again!")
                    .color(NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD))
                return
            }
            player.setMetadata(cooldownKey, org.bukkit.metadata.FixedMetadataValue(this, now + 30_000))
            player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, org.bukkit.SoundCategory.AMBIENT, 1f, 1f)
            player.addPotionEffect(
                org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.STRENGTH,
                    300, // Duration in ticks (15 seconds)
                    1 // Amplifier (0 = lvl1)
                )
            )
            player.sendActionBar(net.kyori.adventure.text.Component
                .text("You feel a power from deep within!")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD))
        }
    }

    @EventHandler
    fun onUseSpeedSword(event: PlayerInteractEvent) {
        val player = event.player
        val cooldownKey = "SwordCooldown"
        val cooldown = player.getMetadata(cooldownKey).firstOrNull()?.asLong() ?: 0L
        val now = System.currentTimeMillis()
        // Only apply effect when right-clicking (use), not left-clicking (attack)
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.item?.type?.name == "NETHERITE_SWORD" &&
            event.item?.itemMeta?.let { meta ->
                meta.hasItemName() && meta.itemName() == net.kyori.adventure.text.Component.text()
                    .content("Sword of Swiftness")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
            if (now < cooldown) {
                player.sendActionBar(net.kyori.adventure.text.Component
                    .text("You must wait before using the ability again!")
                    .color(NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD))
                return
            }
            player.setMetadata(cooldownKey, org.bukkit.metadata.FixedMetadataValue(this, now + 30_000))
            player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, org.bukkit.SoundCategory.AMBIENT, 1f, 1f)
            player.walkSpeed = 0.4f // Doubles walk speed
            thread(start = true) {
                Thread.sleep(15000) // Wait for 15 seconds
                player.walkSpeed = 0.2f // Resets walk speed to normal
                player.sendActionBar(net.kyori.adventure.text.Component
                    .text("The power from within fades.")
                    .color(NamedTextColor.DARK_PURPLE)
                    .decorate(TextDecoration.BOLD))
            }
            player.sendActionBar(net.kyori.adventure.text.Component
                .text("You feel a power from deep within!")
                .color(NamedTextColor.GOLD)
                .decorate(TextDecoration.BOLD))
        }
    }

    @EventHandler
    fun onBoost(event: PlayerInteractEvent) {
        val player = event.player
        val cooldownKey = "SOTWCooldown"
        val cooldown = player.getMetadata(cooldownKey).firstOrNull()?.asLong()?: 0L
        val now = System.currentTimeMillis()
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.player.inventory.itemInMainHand.type == org.bukkit.Material.ECHO_SHARD &&
            event.item?.itemMeta?.let { meta ->
                meta.hasItemName() && meta.itemName() == net.kyori.adventure.text.Component.text()
                    .content("Staff of the Wind")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
            if (now < cooldown) {
                player.sendActionBar(net.kyori.adventure.text.Component
                    .text("You must wait before using the ability again!")
                    .color(NamedTextColor.DARK_RED)
                    .decorate(TextDecoration.BOLD))
                return
            }
            player.setMetadata(cooldownKey, org.bukkit.metadata.FixedMetadataValue(this, now + 500))
            val player = event.player
            val v: Vector = player.location.direction.multiply(1.25) // Boosts player in the direction they are looking
            player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1f)
            player.velocity = v
        }
    }

    fun getRGBFromColor(color: Color): Triple<Double, Double, Double> {
        var red: Double = color.red.toDouble()/255.0f
        var green: Double = color.green.toDouble()/255.0f
        var blue: Double = color.blue.toDouble()/255.0f
        red = String.format("%.2f", red).toDouble()
        green = String.format("%.2f", green).toDouble()
        blue = String.format("%.2f", blue).toDouble()
        return Triple(red, green, blue)
    }

    @EventHandler
    fun onEquippmentChange(event: PlayerArmorChangeEvent) {
        val player: Player = event.player
        val newItem = event.newItem
        if (event.slot == EquipmentSlot.FEET) {
            if (newItem.type == org.bukkit.Material.LEATHER_BOOTS){
                val meta = newItem.itemMeta as LeatherArmorMeta
                val (r,g,b) = getRGBFromColor(meta.color)
                player.sendMessage("Color RGB: ${getRGBFromColor(meta.color)}")
                if ((r == 1.0 && g == 1.0 && b == 1.0) && meta.hasDisplayName() && meta.displayName() == net.kyori.adventure.text.Component.text()
                        .content("Boots of Swiftness")
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                        .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                        .build()
                ) {
                    player.walkSpeed = 0.4f // Increases walk speed when wearing the boots
                    return
                }
                if ((r == 0.0 && g == 0.0 && b == 0.0) && meta.hasDisplayName() && meta.displayName() == net.kyori.adventure.text.Component.text()
                        .content("Boots of Slowness")
                        .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                        .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                        .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                        .build()
                ) {
                    player.walkSpeed = 0.15f // Increases walk speed when wearing the boots
                    player.maxHealth = 40.0 // Sets max health to 3 hearts
                    player.sendMessage("Slowed")
                    return
                }
            } else {
                player.sendMessage("Reset speed")
                player.health = player.health.coerceAtMost(20.0) // Ensures current health does not exceed normal max health
                player.maxHealth = 20.0 // Resets max health to normal if boots are taken off or not the custom item
                player.walkSpeed = 0.2f // Resets walk speed to normal if boots are taken off or not the custom item
            }
        }
    }

    @EventHandler
    fun onCustomMine(event: BlockBreakEvent) {
        val player = event.player
        val block = event.block
        if (player.inventory.itemInMainHand.type == org.bukkit.Material.NETHERITE_PICKAXE &&
            player.inventory.itemInMainHand.itemMeta?.let { meta ->
                meta.hasItemName() && meta.itemName() == net.kyori.adventure.text.Component.text()
                    .content("Pickaxe of Efficiency")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
// Give 64 of the block's natural drop for any block type
            val drops = block.getDrops(player.inventory.itemInMainHand)
            if (drops.isNotEmpty()) {
                drops.forEach { drop ->
                    val stack = drop.clone()
                    stack.amount = 64
                    block.location.world?.dropItemNaturally(block.location.add(0.5, 0.5, 0.5), stack)
                }
                event.isDropItems = false // Prevents normal drops
                player.sendActionBar(net.kyori.adventure.text.Component
                    .text("${player.name} mined a ${block.type.name} and received 64x ${drops.first().type.name}!")
                    .color(NamedTextColor.GREEN)
                    .decorate(TextDecoration.BOLD))
            }
        }
    }
}