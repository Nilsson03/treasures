package ru.nilsson03.treasures.block

import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.block.Block
import org.bukkit.configuration.file.FileConfiguration
import org.bukkit.entity.Player
import ru.nilsson03.library.bukkit.persistense.block.BlockPersistence
import ru.nilsson03.library.bukkit.util.file.ConfigurationUtil
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.file.MessagesFile
import ru.nilsson03.treasures.model.Treasure
import java.util.UUID

class TreasureBlockManager(private val blockPersistence: BlockPersistence) {

    companion object {
        private const val TREASURE_ID_KEY = "treasure_id"
    }

    private val locationCache: MutableMap<UUID, Location> = mutableMapOf()

    fun load() {
        locationCache.clear()
        val config = ConfigurationUtil.load(TreasuresPlugin.instance, "locations.yml")
        val section = config.getConfigurationSection("locations") ?: return

        for (key in section.getKeys(false)) {
            val uuid = try { UUID.fromString(key) } catch (e: Exception) { continue }
            val world = Bukkit.getWorld(config.getString("locations.$key.world") ?: continue) ?: continue
            val x = config.getInt("locations.$key.x")
            val y = config.getInt("locations.$key.y")
            val z = config.getInt("locations.$key.z")
            val location = Location(world, x.toDouble(), y.toDouble(), z.toDouble())
            locationCache[uuid] = location
        }
    }

    fun save() {
        val config = ConfigurationUtil.load(TreasuresPlugin.instance, "locations.yml")
        config.set("locations", null)

        for ((uuid, location) in locationCache) {
            val path = "locations.$uuid"
            config.set("$path.world", location.world?.name)
            config.set("$path.x", location.blockX)
            config.set("$path.y", location.blockY)
            config.set("$path.z", location.blockZ)
        }

        ConfigurationUtil.save(config, TreasuresPlugin.instance.dataFolder, "locations.yml")
    }

    fun setTreasureBlock(player: Player, treasure: Treasure, block: Block) {
        if (hasTreasure(block)) {
            UniversalMessenger.send(player, MessagesFile.getString("messages.commands_set_block_already_exists"))
        } else {
            blockPersistence.set(block, TREASURE_ID_KEY, treasure.uuid.toString())
            blockPersistence.save()
            locationCache[treasure.uuid] = block.location
            save()
            UniversalMessenger.send(player, MessagesFile.getString("messages.commands_set_block_set"))
        }
    }

    fun hasTreasure(block: Block): Boolean {
        if (!blockPersistence.has(block)) return false
        val data = blockPersistence.get(block) ?: return false
        return data.getString(TREASURE_ID_KEY) != null
    }

    fun getTreasureUuid(block: Block): UUID? {
        val data = blockPersistence.get(block) ?: return null
        val id = data.getString(TREASURE_ID_KEY) ?: return null
        return try {
            UUID.fromString(id)
        } catch (e: IllegalArgumentException) {
            null
        }
    }

    fun getLocationByTreasureUuid(uuid: UUID): Location? {
        return locationCache[uuid]
    }
}
