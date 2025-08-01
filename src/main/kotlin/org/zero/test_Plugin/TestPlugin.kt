package org.zero.test_Plugin

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityResurrectEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerJoinEvent
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
    fun onEntityDamage(event: EntityDamageEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            player.playSound(player.location, Sound.BLOCK_BELL_USE, 1f, 1f)
        }
    }

    @EventHandler
    fun onUseTotem(event: EntityResurrectEvent) {
        if (event.entity is Player) {
            val player = event.entity as Player
            player.walkSpeed = 0.4f // Doubles walk speed
            thread(start = true) {
                Thread.sleep(5000) // Wait for 5 seconds
                player.walkSpeed = 0.2f // Resets walk speed to normal
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
            } == true) {
            if (now < cooldown) {
                player.sendMessage("You must wait before using the ability again!")
                return
            }
            player.setMetadata(cooldownKey, org.bukkit.metadata.FixedMetadataValue(this, now + 30_000))
            player.playSound(player.location, Sound.BLOCK_BEACON_ACTIVATE, org.bukkit.SoundCategory.AMBIENT, 1f, 1f)
            player.addPotionEffect(org.bukkit.potion.PotionEffect(
                org.bukkit.potion.PotionEffectType.STRENGTH,
                100, // Duration in ticks (5 seconds)
                4 // Amplifier (0 = lvl1)
            ))
            player.sendMessage("You feel a power from deep within!")
        }
    }

    @EventHandler
    fun onBoost(event: PlayerInteractEvent) {
        if ((event.action.name == "RIGHT_CLICK_AIR" || event.action.name == "RIGHT_CLICK_BLOCK") && event.item?.type?.name == "BREEZE_ROD" &&
            event.item?.itemMeta?.let { meta ->
                meta.hasDisplayName() && meta.displayName() == net.kyori.adventure.text.Component.text()
                    .content("Staff of the Wind")
                    .decorate(net.kyori.adventure.text.format.TextDecoration.BOLD)
                    .decorate(net.kyori.adventure.text.format.TextDecoration.ITALIC)
                    .color(net.kyori.adventure.text.format.NamedTextColor.GOLD)
                    .build()
            } == true) {
        val player = event.player
        val v: Vector = player.location.direction.multiply(1.25) // Boosts player in the direction they are looking
        player.playSound(player.location, Sound.ENTITY_ENDER_DRAGON_FLAP, 1f, 1f)
        player.velocity = v}
    }
}