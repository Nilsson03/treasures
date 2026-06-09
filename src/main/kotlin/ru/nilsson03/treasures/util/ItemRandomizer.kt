package ru.nilsson03.treasures.util

import org.bukkit.inventory.ItemStack
import kotlin.random.Random

class ItemRandomizer {

    private val items: MutableMap<ItemStack, Double> = mutableMapOf()

    fun addItem(item: ItemStack, chance: Double) {
        if (chance <= 0) return
        items[item] = chance
    }

    fun getRandomItem(): ItemStack? {
        if (items.isEmpty()) return null

        val totalWeight = items.values.sum()
        var value = Random.nextDouble() * totalWeight

        for ((item, weight) in items) {
            value -= weight
            if (value <= 0.0) return item
        }
        return null
    }
}
