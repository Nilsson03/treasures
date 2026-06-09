package ru.nilsson03.treasures.listener

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import ru.nilsson03.treasures.block.TreasureBlockManager
import ru.nilsson03.treasures.menu.TreasureMenu
import ru.nilsson03.treasures.service.TreasureService

class TreasureListener(
    private val blockManager: TreasureBlockManager,
    private val treasureService: TreasureService
) : Listener {

    @EventHandler
    fun onClick(event: PlayerInteractEvent) {
        if (event.action != Action.RIGHT_CLICK_BLOCK) return

        val block = event.clickedBlock ?: return
        if (!blockManager.hasTreasure(block)) return

        val treasureUuid = blockManager.getTreasureUuid(block) ?: return
        val plugin = ru.nilsson03.treasures.TreasuresPlugin.instance
        val treasure = plugin.treasureRepository.getTreasureByUuid(treasureUuid) ?: return

        event.isCancelled = true
        TreasureMenu.open(event.player, treasure, block)
    }
}
