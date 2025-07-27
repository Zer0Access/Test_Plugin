package org.zero.test_Plugin

import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.plugin.java.JavaPlugin
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.entity.EntityResurrectEvent
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
}