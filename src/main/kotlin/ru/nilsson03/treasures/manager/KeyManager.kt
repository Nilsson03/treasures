package ru.nilsson03.treasures.manager

import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.and
import org.jetbrains.exposed.sql.insert
import org.jetbrains.exposed.sql.select
import org.jetbrains.exposed.sql.transactions.transaction
import org.jetbrains.exposed.sql.update
import ru.nilsson03.treasures.database.DatabaseManager
import ru.nilsson03.treasures.database.TreasureOpens
import ru.nilsson03.treasures.database.Users
import ru.nilsson03.treasures.model.Treasure
import java.util.UUID

class KeyManager(private val databaseManager: DatabaseManager) {

    fun getKeys(playerUuid: UUID, treasure: Treasure): Int {
        return transaction {
            ensureUserExists(playerUuid)
            TreasureOpens.select {
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }.firstOrNull()?.get(TreasureOpens.keys) ?: 0
        }
    }

    fun giveKeys(playerUuid: UUID, treasure: Treasure, amount: Int) {
        transaction {
            ensureUserExists(playerUuid)
            val existing = TreasureOpens.select {
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }.firstOrNull()

            if (existing != null) {
                TreasureOpens.update({
                    (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
                }) {
                    it[keys] = existing[keys] + amount
                }
            } else {
                TreasureOpens.insert {
                    it[userId] = playerUuid
                    it[treasureId] = treasure.uuid
                    it[keys] = amount
                    it[count] = 0
                }
            }
        }
    }

    fun takeKeys(playerUuid: UUID, treasure: Treasure, amount: Int) {
        transaction {
            ensureUserExists(playerUuid)
            val existing = TreasureOpens.select {
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }.firstOrNull() ?: return@transaction

            val newKeys = (existing[TreasureOpens.keys] - amount).coerceAtLeast(0)
            TreasureOpens.update({
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }) {
                it[keys] = newKeys
            }
        }
    }

    fun hasKey(playerUuid: UUID, treasure: Treasure): Boolean {
        return getKeys(playerUuid, treasure) > 0
    }

    fun useKey(playerUuid: UUID, treasure: Treasure) {
        transaction {
            ensureUserExists(playerUuid)
            val existing = TreasureOpens.select {
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }.firstOrNull() ?: return@transaction

            TreasureOpens.update({
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }) {
                it[keys] = (existing[TreasureOpens.keys] - 1).coerceAtLeast(0)
                it[count] = existing[TreasureOpens.count] + 1
            }
        }
    }

    fun getOpenCount(playerUuid: UUID, treasure: Treasure): Int {
        return transaction {
            ensureUserExists(playerUuid)
            TreasureOpens.select {
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }.firstOrNull()?.get(TreasureOpens.count) ?: 0
        }
    }

    private fun ensureUserExists(playerUuid: UUID) {
        val exists = Users.select { Users.id eq playerUuid }.firstOrNull()
        if (exists == null) {
            Users.insert { it[id] = playerUuid }
        }
    }
}
