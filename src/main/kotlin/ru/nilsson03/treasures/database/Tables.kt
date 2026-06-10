package ru.nilsson03.treasures.database

import org.jetbrains.exposed.sql.Table

object Users : Table("treasures_users") {
    val id = uuid("id")
    override val primaryKey = PrimaryKey(id)
}

object TreasureOpens : Table("treasures_opens") {
    val id = integer("id").autoIncrement()
    val userId = uuid("user_id").references(Users.id)
    val treasureId = uuid("treasure_id")
    val keys = integer("keys").default(0)
    val count = integer("count").default(0)
    override val primaryKey = PrimaryKey(id)
}
