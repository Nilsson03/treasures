package ru.nilsson03.treasures.animation

import org.bukkit.Location

class TreasureOpener {

    private val openingTreasures: MutableSet<Location> = mutableSetOf()

    fun isTreasureOpening(location: Location): Boolean = openingTreasures.contains(location)

    fun startOpeningTreasure(location: Location) {
        openingTreasures.add(location)
    }

    fun finishOpeningTreasure(location: Location) {
        openingTreasures.remove(location)
    }
}
