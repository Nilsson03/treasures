package ru.nilsson03.treasures.animation

import eu.decentsoftware.holograms.api.DHAPI
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.scheduler.BukkitRunnable
import org.bukkit.util.Vector
import ru.nilsson03.library.bukkit.util.TranslationUtil
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.file.MessagesFile
import ru.nilsson03.treasures.model.Treasure
import ru.nilsson03.treasures.util.ItemRandomizer

object TreasureAnimation {

    private const val HEIGHT = 6
    private const val SPEED = 0.5

    fun playOpenAnimation(
            location: Location,
            treasure: Treasure,
            reward: ItemStack,
            player: Player
    ) {
        val startLocation = location.clone()
        val plugin = TreasuresPlugin.instance
        val treasureOpener = plugin.treasureService.treasureOpener

        val itemRandomizer = ItemRandomizer()
        treasure.items.forEach { (item, chance) -> itemRandomizer.addItem(item, chance) }
        val displayItem = itemRandomizer.getRandomItem() ?: reward.clone()

        val item = location.world!!.dropItem(location.clone().add(0.5, 1.0, 0.5), displayItem)

        val hologram = DHAPI.getHologram(treasure.uuid.toString())
        hologram?.disable()

        item.pickupDelay = 32767

        object : BukkitRunnable() {
                    var isFalling = false

                    override fun run() {
                        if (item.location.y >= location.y + HEIGHT && !isFalling) {
                            isFalling = true
                            item.velocity = Vector(0.0, -SPEED, 0.0)
                            item.itemStack = reward
                        } else if (item.location.y <= location.y + 1.5 && isFalling) {
                            cancel()
                            object : BukkitRunnable() {
                                        override fun run() {
                                            item.remove()
                                            player.inventory.addItem(reward)
                                            treasureOpener.finishOpeningTreasure(startLocation)
                                            hologram?.enable()
                                        }
                                    }
                                    .runTaskLater(plugin, 40L)

                            val itemName =
                                    if (item.itemStack.itemMeta?.hasDisplayName() == true) {
                                        item.itemStack.itemMeta!!.displayName
                                    } else {
                                        TranslationUtil.translateItem(item.itemStack)
                                    }

                            Bukkit.getOnlinePlayers().forEach { onlinePlayer ->
                                UniversalMessenger.send(
                                        onlinePlayer,
                                        MessagesFile.getList(
                                                "messages.treasure_reward",
                                                ReplaceData("{player}", player.name),
                                                ReplaceData("{treasure}", treasure.displayName),
                                                ReplaceData("{item}", itemName),
                                                ReplaceData("{count}", item.itemStack.amount)
                                        )
                                )
                            }
                        } else if (!isFalling) {
                            val randomItem = itemRandomizer.getRandomItem()
                            if (randomItem != null) {
                                item.itemStack = randomItem
                            }
                            item.velocity = Vector(0.0, SPEED, 0.0)
                            location.world?.playSound(
                                    item.location,
                                    Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE,
                                    1f,
                                    1f
                            )
                            location.world?.spawnParticle(
                                    Particle.FLAME,
                                    item.location,
                                    10,
                                    0.0,
                                    0.0,
                                    0.0,
                                    0.0
                            )
                        }
                    }
                }
                .runTaskTimer(plugin, 0L, 1L)
    }
}
