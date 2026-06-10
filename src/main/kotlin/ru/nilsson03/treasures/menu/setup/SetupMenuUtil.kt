package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder
import ru.nilsson03.library.bukkit.item.builder.impl.UniversalSkullBuilder
import ru.nilsson03.library.bukkit.item.skull.factory.SkullHandlerFactory
import ru.nilsson03.library.invui.gui.ScrollGui
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.controlitem.ScrollItem

object SetupMenuUtil {

    fun skullBuilder(): UniversalSkullBuilder {
        return UniversalSkullBuilder(SkullHandlerFactory.createHandler())
    }

    fun scrollUpButton(): ScrollItem {
        return object : ScrollItem(-1) {
            override fun getItemProvider(gui: ScrollGui<*>): ItemProvider {
                return ItemProvider { _ ->
                    SpigotItemBuilder(Material.ARROW)
                        .setDisplayName("§6Вверх")
                        .build()
                }
            }
        }
    }

    fun scrollDownButton(): ScrollItem {
        return object : ScrollItem(1) {
            override fun getItemProvider(gui: ScrollGui<*>): ItemProvider {
                return ItemProvider { _ ->
                    SpigotItemBuilder(Material.ARROW)
                        .setDisplayName("§6Вниз")
                        .build()
                }
            }
        }
    }
}
