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
        val jdbcUrl = config.getString("database.jdbc-url") ?: "jdbc:mysql://localhost:3306/treasures"
        val username = config.getString("database.username") ?: "root"
        val password = config.getString("database.password") ?: ""

        SharedPoolRegistry.registerPlugin(plugin, "treasures", jdbcUrl, username, password)
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
