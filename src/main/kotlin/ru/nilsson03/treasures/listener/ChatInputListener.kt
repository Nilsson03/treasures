package ru.nilsson03.treasures.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent
import org.bukkit.event.player.PlayerCommandPreprocessEvent
import org.bukkit.event.player.PlayerQuitEvent
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.util.ChatInputHandler

class ChatInputListener : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onChat(event: AsyncPlayerChatEvent) {
        if (!ChatInputHandler.hasPendingInput(event.player)) return

        event.isCancelled = true
        val message = event.message
        val player = event.player

        TreasuresPlugin.instance.server.scheduler.runTask(TreasuresPlugin.instance, Runnable {
            ChatInputHandler.handleInput(player, message)
        })
    }

    @EventHandler(priority = EventPriority.LOWEST)
    fun onCommand(event: PlayerCommandPreprocessEvent) {
        if (!ChatInputHandler.hasPendingInput(event.player)) return

        event.isCancelled = true
        val message = event.message.removePrefix("/")
        val player = event.player

        TreasuresPlugin.instance.server.scheduler.runTask(TreasuresPlugin.instance, Runnable {
            ChatInputHandler.handleInput(player, message)
        })
    }

    @EventHandler
    fun onQuit(event: PlayerQuitEvent) {
        ChatInputHandler.removePlayer(event.player)
    }
}
