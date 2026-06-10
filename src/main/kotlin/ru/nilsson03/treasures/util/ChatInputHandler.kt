package ru.nilsson03.treasures.util

import org.bukkit.entity.Player
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object ChatInputHandler {

    private val pendingInputs = ConcurrentHashMap<UUID, (String) -> Unit>()

    fun addPlayer(player: Player, callback: (String) -> Unit) {
        pendingInputs[player.uniqueId] = callback
    }

    fun hasPendingInput(player: Player): Boolean {
        return pendingInputs.containsKey(player.uniqueId)
    }

    fun handleInput(player: Player, message: String): Boolean {
        val callback = pendingInputs.remove(player.uniqueId) ?: return false
        callback(message)
        return true
    }

    fun removePlayer(player: Player) {
        pendingInputs.remove(player.uniqueId)
    }
}
