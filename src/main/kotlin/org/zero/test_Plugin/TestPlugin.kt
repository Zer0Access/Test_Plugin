package org.zero.test_Plugin

import com.destroystokyo.paper.event.player.PlayerArmorChangeEvent
import io.papermc.paper.datacomponent.item.DyedItemColor
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
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemType
import org.bukkit.inventory.meta.LeatherArmorMeta
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.util.Vector
import sun.font.EAttribute
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
            var success = player.addPotionEffect(
                org.bukkit.potion.PotionEffect(
                    org.bukkit.potion.PotionEffectType.JUMP_BOOST,
                    300, // Duration in ticks (15 seconds)
                    0, // Amplifier (0 = lvl1)
                    true, // Ambient
                    true, // Shows particles
                    false // Shows icon
                )

            )
            println("Potion effect applied: $success")
            thread(start = true) {
                Thread.sleep(5000) // Wait for 5 seconds
                player.walkSpeed = 0.2f // Resets walk speed to normal
                player.sendMessage ("Your boost of swiftness has worn off.")
            }
        }
    }

    @EventHandler
    fun onUseCustomSword(event: PlayerInteractEvent) {
        val player = event.player
        val cooldownKey = "SwordCooldown"
        val cooldown = player.getMetadata(cooldownKey).firstOrNull()?.asLong() ?: 0L
        val now = System.currentTimeMillis()
        // Only apply effect when right-clicking (use), not left-clicking (attack)
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.item?.type?.name == "NETHERITE_SWORD" &&
            event.item?.itemMeta?.let { meta ->
                meta.hasDisplayName() && meta.displayName() == net.kyori.adventure.text.Component.text()
                    .content("Custom Sword")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
            if (now < cooldown) {
                player.sendMessage("You must wait before using the ability again!")
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
            player.sendMessage("You feel a power from deep within!")
        }
    }

    @EventHandler
    fun onBoost(event: PlayerInteractEvent) {
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.player.inventory.itemInMainHand.type == org.bukkit.Material.STICK &&
            event.item?.itemMeta?.let { meta ->
                meta.hasDisplayName() && meta.displayName() == net.kyori.adventure.text.Component.text()
                    .content("Staff of the Wind")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true
        ) {
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
                    player.walkSpeed = 0.1f // Increases walk speed when wearing the boots
                    player.sendMessage("Slowed")
                    return
                }
            } else {
                player.sendMessage("Reset speed")
                player.walkSpeed = 0.2f // Resets walk speed to normal if boots are taken off or not the custom item
            }
        }
    }
}