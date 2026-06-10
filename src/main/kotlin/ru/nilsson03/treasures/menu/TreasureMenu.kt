package ru.nilsson03.treasures.menu

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import ru.nilsson03.library.invui.gui.Gui
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.AutoUpdateItem
import ru.nilsson03.library.invui.item.impl.SimpleItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.file.InventoriesFile
import ru.nilsson03.treasures.model.Treasure
import java.util.function.Supplier

object TreasureMenu {

    fun open(player: Player, treasure: Treasure, block: Block) {
        val plugin = TreasuresPlugin.instance
        val keyManager = plugin.keyManager
        val treasureService = plugin.treasureService

        val itemsChar = InventoriesFile.getString("inventories.main_menu.items.default.char").first()

        val treasureItem = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                val builder = treasure.displayInventoryItem?.clone() ?: ItemStack(Material.CHEST)
                val meta = builder.itemMeta
                meta?.addItemFlags(ItemFlag.HIDE_ATTRIBUTES)
                meta?.setDisplayName(
                    InventoriesFile.getString(
                        "inventories.main_menu.items.default.displayName",
                        ReplaceData("{keys}", keyManager.getKeys(player.uniqueId, treasure)),
                        ReplaceData("{displayName}", treasure.displayName),
                        ReplaceData("{lore}", treasure.lore.joinToString("\n"))
                    )
                )
                meta?.lore = InventoriesFile.getList(
                    "inventories.main_menu.items.default.lore",
                    ReplaceData("{keys}", keyManager.getKeys(player.uniqueId, treasure)),
                    ReplaceData("{displayName}", treasure.displayName),
                    ReplaceData("{lore}", treasure.lore.joinToString("\n")),
                    ReplaceData("{opens}", keyManager.getOpenCount(player.uniqueId, treasure)),
                    ReplaceData("{progress}", treasureService.getProgressBar(player.uniqueId, treasure))
                )
                builder.itemMeta = meta
                builder
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                treasureService.openTreasure(player, block)
            }
        }

        val previewChar = InventoriesFile.getString("inventories.main_menu.items.preview.char").first()

        val previewItem = SimpleItem(ItemProvider { _ ->
            val material = Material.valueOf(
                InventoriesFile.getString("inventories.main_menu.items.preview.item.material").uppercase()
            )
            val item = ItemStack(material)
            val meta = item.itemMeta
            meta?.setDisplayName(InventoriesFile.getString("inventories.main_menu.items.preview.displayName"))
            meta?.lore = InventoriesFile.getList("inventories.main_menu.items.preview.lore")
            item.itemMeta = meta
            item
        }) { _ ->
            PreviewMenu.open(player, treasure)
        }

        val structure = InventoriesFile.getList("inventories.main_menu.structure").toTypedArray()

        val gui = Gui.normal()
            .setStructure(*structure)
            .addIngredient(itemsChar, treasureItem)
            .addIngredient(previewChar, previewItem)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle(InventoriesFile.getString("inventories.main_menu.title"))
            .setGui(gui)
            .build()
            .open()
    }
}
