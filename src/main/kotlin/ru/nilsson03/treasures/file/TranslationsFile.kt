package ru.nilsson03.treasures.file

import org.bukkit.Material
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.PotionMeta
import org.bukkit.inventory.meta.SkullMeta
import ru.nilsson03.library.bukkit.util.file.ConfigurationUtil
import ru.nilsson03.library.text.api.UniversalTextApi
import ru.nilsson03.treasures.TreasuresPlugin

object TranslationsFile {

    private lateinit var config: FileConfiguration

    fun load() {
        config = ConfigurationUtil.load(TreasuresPlugin.instance, "translation.yml")
    }

    fun getTranslationItem(itemStack: ItemStack): String {
        val material = itemStack.type

        if (material == Material.TIPPED_ARROW) {
            val meta = itemStack.itemMeta as? PotionMeta
            if (meta?.basePotionData != null) {
                val effectType = meta.basePotionData.type.effectType
                return getTranslation("items.TIPPED_ARROW.EFFECT.${effectType?.name}")
            }
        }

        if (material == Material.PLAYER_HEAD) {
            val meta = itemStack.itemMeta as? SkullMeta
            if (meta != null) {
                val owner = meta.owningPlayer?.name ?: "Unknown"
                return getTranslation("items.PLAYER_HEAD.NAMED").replace("%s", owner)
            }
        }

        return getTranslation("items.${material.name}")
    }

    private fun getTranslation(path: String): String {
        return UniversalTextApi.colorize(config.getString(path) ?: "§c$path")
    }
}
