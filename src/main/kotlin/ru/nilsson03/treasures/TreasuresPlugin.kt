package ru.nilsson03.treasures

import org.bukkit.plugin.java.JavaPlugin

class TreasuresPlugin : JavaPlugin() {

    override fun onEnable() {
        logger.info("Treasures enabled!")
    }

    override fun onDisable() {
        logger.info("Treasures disabled!")
    }
}
