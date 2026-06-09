package ru.nilsson03.treasures.database

import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.transactions.transaction
import ru.nilsson03.hikaricp.SharedPoolRegistry
import ru.nilsson03.library.bukkit.util.log.ConsoleLogger
import ru.nilsson03.treasures.TreasuresPlugin

class DatabaseManager(private val plugin: TreasuresPlugin) {

    private var database: Database? = null

    fun connect() {
        val config = plugin.config
        val host = config.getString("database.host") ?: "localhost"
        val port = config.getInt("database.port", 3306)
        val dbName = config.getString("database.database") ?: "treasures"
        val user = config.getString("database.user") ?: "root"
        val password = config.getString("database.password") ?: ""

        SharedPoolRegistry.registerPlugin(plugin, "$host:$port", dbName, user, password)
        val pool = SharedPoolRegistry.getPool(plugin)

        database = Database.connect(pool.dataSource)

        transaction {
            SchemaUtils.createMissingTablesAndColumns(Users, TreasureOpens)
        }

        ConsoleLogger.info(plugin, "Подключение к базе данных установлено")
    }

    fun disconnect() {
        SharedPoolRegistry.unregisterPlugin(plugin)
        ConsoleLogger.info(plugin, "Подключение к базе данных закрыто")
    }
}
