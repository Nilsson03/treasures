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
            }.firstOrNull()?.get(TreasureOpens.count) ?: 0
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
                    it[count] = existing[count] + amount
                }
            } else {
                TreasureOpens.insert {
                    it[userId] = playerUuid
                    it[treasureId] = treasure.uuid
                    it[count] = amount
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

            val newCount = (existing[TreasureOpens.count] - amount).coerceAtLeast(0)
            TreasureOpens.update({
                (TreasureOpens.userId eq playerUuid) and (TreasureOpens.treasureId eq treasure.uuid)
            }) {
                it[count] = newCount
            }
        }
    }

    fun hasKey(playerUuid: UUID, treasure: Treasure): Boolean {
        return getKeys(playerUuid, treasure) > 0
    }

    fun useKey(playerUuid: UUID, treasure: Treasure) {
        takeKeys(playerUuid, treasure, 1)
    }

    fun getOpenCount(playerUuid: UUID, treasure: Treasure): Int {
        return getKeys(playerUuid, treasure)
    }

    private fun ensureUserExists(playerUuid: UUID) {
        val exists = Users.select { Users.id eq playerUuid }.firstOrNull()
        if (exists == null) {
            Users.insert { it[id] = playerUuid }
        }
    }
}
