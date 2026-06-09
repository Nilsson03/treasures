package ru.nilsson03.treasures.menu

import org.bukkit.entity.Player
import ru.nilsson03.library.invui.gui.ScrollGui
import ru.nilsson03.library.invui.gui.structure.Markers
import ru.nilsson03.library.invui.item.Item
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.SimpleItem
import ru.nilsson03.library.invui.item.impl.controlitem.ScrollItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.file.InventoriesFile
import ru.nilsson03.treasures.model.Treasure

object PreviewMenu {

    fun open(player: Player, treasure: Treasure) {
        val items: List<Item> = treasure.items.entries.map { (itemStack, chance) ->
            SimpleItem(ItemProvider { _ ->
                val clone = itemStack.clone()
                val meta = clone.itemMeta
                meta?.lore = InventoriesFile.getList(
                    "inventories.preview_menu.items.default.lore",
                    ReplaceData("{chance}", chance)
                )
                clone.itemMeta = meta
                clone
            })
        }

        val structure = InventoriesFile.getList("inventories.preview_menu.structure").toTypedArray()

        val gui = ScrollGui.items()
            .setStructure(*structure)
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .setContent(items)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle(InventoriesFile.getString("inventories.preview_menu.title"))
            .setGui(gui)
            .build()
            .open()
    }
}
