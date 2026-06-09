package ru.nilsson03.treasures

import org.bukkit.Bukkit
import ru.nilsson03.library.NPlugin
import ru.nilsson03.library.bukkit.integration.PluginInfo
import ru.nilsson03.library.bukkit.persistense.block.BlockPersistence
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger
import ru.nilsson03.treasures.command.TreasuresCommand
import ru.nilsson03.treasures.database.DatabaseManager
import ru.nilsson03.treasures.file.Config
import ru.nilsson03.treasures.file.InventoriesFile
import ru.nilsson03.treasures.file.MessagesFile
import ru.nilsson03.treasures.file.TranslationsFile
import ru.nilsson03.treasures.block.TreasureBlockManager
import ru.nilsson03.treasures.listener.TreasureListener
import ru.nilsson03.treasures.manager.KeyManager
import ru.nilsson03.treasures.animation.TreasureOpener
import ru.nilsson03.treasures.repository.TreasureRepository
import ru.nilsson03.treasures.service.TreasureService

class TreasuresPlugin : NPlugin() {

    lateinit var treasureService: TreasureService
        private set
    lateinit var keyManager: KeyManager
        private set
    lateinit var treasureBlockManager: TreasureBlockManager
        private set
    lateinit var treasureRepository: TreasureRepository
        private set
    lateinit var blockPersistence: BlockPersistence
        private set
    lateinit var databaseManager: DatabaseManager
        private set

    override fun enable() {
        instance = this
        val millis = System.currentTimeMillis()

        ConsoleLogger.register(this, true)

        saveDefaultConfig()

        integration().addDependency(
            PluginInfo("DecentHolograms", "2.8.0")
        )

        Config.load()
        MessagesFile.load()
        InventoriesFile.load()
        TranslationsFile.load()

        databaseManager = DatabaseManager(this)
        databaseManager.connect()

        keyManager = KeyManager(databaseManager)

        blockPersistence = BlockPersistence(this)
        treasureBlockManager = TreasureBlockManager(blockPersistence)
        treasureBlockManager.load()

        treasureRepository = TreasureRepository(this)
        treasureRepository.load()

        val treasureOpener = TreasureOpener()
        treasureService = TreasureService(this, keyManager, treasureBlockManager, treasureOpener)

        getCommand("treasures")?.let { cmd ->
            val command = TreasuresCommand(treasureService, treasureBlockManager, keyManager)
            cmd.setExecutor(command)
            cmd.tabCompleter = command
        }

        Bukkit.getPluginManager().registerEvents(TreasureListener(treasureBlockManager, treasureService), this)

        ConsoleLogger.success(this, "Плагин загружен за %dms", System.currentTimeMillis() - millis)
    }

    override fun disable() {
        taskScheduler().shutdown()

        if (::treasureRepository.isInitialized) {
            treasureRepository.save()
        }

        if (::treasureBlockManager.isInitialized) {
            treasureBlockManager.save()
        }

        if (::blockPersistence.isInitialized) {
            blockPersistence.save()
        }

        if (::databaseManager.isInitialized) {
            databaseManager.disconnect()
        }

        ConsoleLogger.unregister(this)
    }

    companion object {
        lateinit var instance: TreasuresPlugin
            private set
    }
}
