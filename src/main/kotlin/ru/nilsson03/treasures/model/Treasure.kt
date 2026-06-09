package ru.nilsson03.treasures.model

import org.bukkit.inventory.ItemStack
import java.util.UUID

data class Treasure(
    val uuid: UUID,
    var displayName: String = "",
    var lore: List<String> = emptyList(),
    var items: MutableMap<ItemStack, Double> = mutableMapOf(),
    var displayInventoryItem: ItemStack? = null,
    var needOpensToReward: Int = 10,
    var rewardCommands: List<String> = emptyList(),
    var hologramLines: List<String> = emptyList()
)
