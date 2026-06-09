package ru.nilsson03.treasures.service

import eu.decentsoftware.holograms.api.DHAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.util.Vector
import ru.nilsson03.library.text.api.UniversalTextApi
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.animation.TreasureAnimation
import ru.nilsson03.treasures.animation.TreasureOpener
import ru.nilsson03.treasures.block.TreasureBlockManager
import ru.nilsson03.treasures.file.Config
import ru.nilsson03.treasures.file.MessagesFile
import ru.nilsson03.treasures.manager.KeyManager
import ru.nilsson03.treasures.model.Treasure
import ru.nilsson03.treasures.util.ItemRandomizer

class TreasureService(
    private val plugin: TreasuresPlugin,
    private val keyManager: KeyManager,
    private val blockManager: TreasureBlockManager,
    val treasureOpener: TreasureOpener
) {

    fun loadHolograms() {
        val treasures = plugin.treasureRepository.getTreasures()

        for (treasure in treasures) {
            val location = blockManager.getLocationByTreasureUuid(treasure.uuid) ?: continue

            val hologramLocation = location.clone().add(0.5, 1.5 + treasure.hologramLines.size * 0.3, 0.5)
            val hologram = DHAPI.createHologram(treasure.uuid.toString(), hologramLocation)

            for (line in treasure.hologramLines) {
                DHAPI.addHologramLine(hologram, UniversalTextApi.colorize(line))
            }
        }
    }

    fun getProgressBar(playerUuid: java.util.UUID, treasure: Treasure): String {
        var current = keyManager.getOpenCount(playerUuid, treasure)
        val needOpens = treasure.needOpensToReward

        if (current > needOpens) {
            val rewardCount = current / needOpens
            current -= rewardCount * needOpens
        }

        val width = Config.progressBarWidth
        val yes = Config.progressBarYes
        val no = Config.progressBarNo
        val symbol = Config.progressBarSymbol

        val filled = (current.toDouble() / needOpens * width).toInt().coerceIn(0, width)
        val empty = width - filled

        return "$yes${symbol.repeat(filled)}$no${symbol.repeat(empty)}"
    }

    fun openTreasure(player: Player, block: Block) {
        if (!blockManager.hasTreasure(block)) return

        val treasureUuid = blockManager.getTreasureUuid(block) ?: return
        val treasure = plugin.treasureRepository.getTreasureByUuid(treasureUuid) ?: return

        if (treasureOpener.isTreasureOpening(block.location)) {
            player.closeInventory()
            UniversalMessenger.send(player, MessagesFile.getString("messages.treasure_already_opening"))
            return
        }

        if (!keyManager.hasKey(player.uniqueId, treasure)) {
            UniversalMessenger.send(
                player,
                MessagesFile.getString("messages.treasure_dont_have_keys", ReplaceData("{treasure}", treasure.displayName))
            )
            player.closeInventory()
            val direction = player.location.subtract(block.location).toVector().normalize()
            player.velocity = direction.multiply(2.5)
            return
        }

        val itemRandomizer = ItemRandomizer()
        treasure.items.forEach { (item, chance) -> itemRandomizer.addItem(item, chance) }
        val reward = itemRandomizer.getRandomItem()

        if (reward != null) {
            keyManager.useKey(player.uniqueId, treasure)
            treasureOpener.startOpeningTreasure(block.location)
            TreasureAnimation.playOpenAnimation(block.location, treasure, reward, player)

            checkRewardCommands(player, treasure)
        } else {
            UniversalMessenger.send(player, MessagesFile.getString("messages.treasure_no_reward"))
        }

        player.closeInventory()
    }

    private fun checkRewardCommands(player: Player, treasure: Treasure) {
        val current = keyManager.getOpenCount(player.uniqueId, treasure)
        val needOpens = treasure.needOpensToReward

        if (current >= needOpens && current % needOpens == 0) {
            for (command in treasure.rewardCommands) {
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command.replace("{player}", player.name))
            }
        }
    }
}
