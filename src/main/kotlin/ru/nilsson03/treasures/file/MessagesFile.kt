package ru.nilsson03.treasures.file

import org.bukkit.configuration.file.FileConfiguration
import ru.nilsson03.library.bukkit.util.file.ConfigurationUtil
import ru.nilsson03.library.text.api.UniversalTextApi
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.TreasuresPlugin

object MessagesFile {

    private lateinit var config: FileConfiguration

    fun load() {
        config = ConfigurationUtil.load(TreasuresPlugin.instance, "messages.yml")
    }

    fun getString(path: String, vararg replacements: ReplaceData): String {
        var message = config.getString(path) ?: return "§c$path not found"
        replacements.forEach {
            message = message.replace(it.key, it.`object`.toString())
        }
        return UniversalTextApi.colorize(message)
    }

    fun getList(path: String, vararg replacements: ReplaceData): List<String> {
        return config.getStringList(path).map { line ->
            var result = line
            replacements.forEach { result = result.replace(it.key, it.`object`.toString()) }
            UniversalTextApi.colorize(result)
        }
    }
}
