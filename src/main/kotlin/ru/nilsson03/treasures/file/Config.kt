package ru.nilsson03.treasures.file

import ru.nilsson03.treasures.TreasuresPlugin

object Config {

    var progressBarWidth: Int = 10
        private set
    var progressBarYes: String = "&d"
        private set
    var progressBarNo: String = "&8"
        private set
    var progressBarSymbol: String = "∎"
        private set

    fun load() {
        val config = TreasuresPlugin.instance.config
        progressBarWidth = config.getInt("settings.progress_bar_width", 10)
        progressBarYes = config.getString("settings.progress_bar_yes") ?: "&d"
        progressBarNo = config.getString("settings.progress_bar_no") ?: "&8"
        progressBarSymbol = config.getString("settings.progress_bar_symbol") ?: "∎"
    }
}
