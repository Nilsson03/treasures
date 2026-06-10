package ru.nilsson03.treasures.repository

import java.util.UUID
import org.bukkit.inventory.ItemStack
import ru.nilsson03.library.bukkit.util.file.ConfigurationUtil
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger
import ru.nilsson03.library.text.api.UniversalTextApi
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.model.Treasure

class TreasureRepository(private val plugin: TreasuresPlugin) {

    private val treasures: MutableList<Treasure> = mutableListOf()

    fun load() {
        treasures.clear()
        val config = ConfigurationUtil.load(plugin, "treasures.yml")

        val section = config.getConfigurationSection("treasures") ?: return

        for (key in section.getKeys(false)) {
            val uuid =
                    try {
                        UUID.fromString(key)
                    } catch (e: Exception) {
                        continue
                    }
            val path = "treasures.$key"

            val displayName = UniversalTextApi.colorize(config.getString("$path.displayName") ?: "")
            val lore = config.getStringList("$path.lore").map { UniversalTextApi.colorize(it) }
            val needOpensToReward = config.getInt("$path.needOpensToReward", 10)
            val rewardCommands = config.getStringList("$path.rewardsCommands")
            val hologramLines = config.getStringList("$path.hologram")

            val items = mutableMapOf<ItemStack, Double>()
            val itemsSection = config.getConfigurationSection("$path.items")
            if (itemsSection != null) {
                for (itemKey in itemsSection.getKeys(false)) {
                    val itemSection = config.getConfigurationSection("$path.items.$itemKey.item")
                    if (itemSection == null) {
                        ConsoleLogger.warn(plugin, "Missing item section for $path.items.$itemKey")
                        continue
                    }

                    val itemStack =
                            try {
                                ItemStack.deserialize(itemSection.getValues(true))
                            } catch (e: Exception) {
                                ConsoleLogger.error(
                                        plugin,
                                        "Failed to deserialize item: ${e.message}"
                                )
                                null
                            }

                    if (itemStack == null) continue

                    val chance = config.getDouble("$path.items.$itemKey.chance")
                    items[itemStack] = chance
                }
            }

            val inventoryItemSection = config.getConfigurationSection("$path.inventoryItem")
            val inventoryItem =
                    inventoryItemSection?.let { ItemStack.deserialize(it.getValues(true)) }

            treasures.add(
                    Treasure(
                            uuid = uuid,
                            displayName = displayName,
                            lore = lore,
                            items = items,
                            displayInventoryItem = inventoryItem,
                            needOpensToReward = needOpensToReward,
                            rewardCommands = rewardCommands,
                            hologramLines = hologramLines
                    )
            )
        }
    }

    fun save() {
        val config = plugin.treasuresConfig
        config.set("treasures", null)

        for (treasure in treasures) {
            val path = "treasures.${treasure.uuid}"
            config.set("$path.displayName", treasure.displayName)
            config.set("$path.lore", treasure.lore)
            config.set("$path.needOpensToReward", treasure.needOpensToReward)
            config.set("$path.rewardsCommands", treasure.rewardCommands)
            config.set("$path.hologram", treasure.hologramLines)

            if (treasure.displayInventoryItem != null) {
                config.set("$path.inventoryItem", treasure.displayInventoryItem!!.serialize())
            }

            for ((itemStack, chance) in treasure.items) {
                val itemUuid = UUID.randomUUID()
                config.set("$path.items.$itemUuid.item", itemStack.serialize())
                config.set("$path.items.$itemUuid.chance", chance)
            }
        }

        ConfigurationUtil.save(config, plugin.dataFolder, "treasures.yml")
    }

    fun addTreasure(treasure: Treasure) {
        treasures.add(treasure)
    }

    fun removeTreasure(treasure: Treasure) {
        treasures.remove(treasure)
    }

    fun getTreasureByUuid(uuid: UUID): Treasure? = treasures.find { it.uuid == uuid }

    fun getTreasures(): List<Treasure> = treasures
}
