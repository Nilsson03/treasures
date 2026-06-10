package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import org.bukkit.entity.Player
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder
import ru.nilsson03.library.invui.gui.Gui
import ru.nilsson03.library.invui.inventory.VirtualInventory
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.SuppliedItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.skullBuilder
import ru.nilsson03.treasures.model.Treasure
import java.util.function.Supplier

object TreasureAddDropItemsMenu {

    fun open(player: Player, treasure: Treasure) {
        val virtualInventory = VirtualInventory(45)

        val closeButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Назад")
                        .setLore(listOf("§7Нажмите, чтобы вернуться"))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M1ZmQ0MTJiY2VjNDhmMjk4MDIyOWY0NGFkNzg1ZTlkMjQ3ZjY2YjZhOTFlZWY3YTk4ZDc2NmJkMTFkNGExOSJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            TreasureListItemsMenu.open(player, treasure)
            true
        }

        val saveButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Сохранить предметы")
                        .setLore(listOf(
                            "",
                            "§7Нажмите, чтобы сохранить выбранные",
                            "§7предметы в сокровищницу",
                            "",
                            "§8▹ §eНажмите, чтобы сохранить"
                        ))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjlmMjc3NzNmMDhkY2M2ODU0NzNhMjFmNjQ3NmZlYjZhYWQ4OWFjMjRhMTNmMDRlZjYzNjkyYjlhYzlmZWE5MCJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            var added = 0
            for (i in 0 until virtualInventory.size) {
                val item = virtualInventory.getItem(i)
                if (item != null && item.type != Material.AIR) {
                    treasure.items[item.clone()] = 0.0
                    added++
                }
            }
            UniversalMessenger.send(player, "§a✔ §fДобавлено предметов: §e$added")
            TreasureListItemsMenu.open(player, treasure)
            true
        }

        val gui = Gui.normal()
            .setStructure(
                "v v v v v v v v v",
                "v v v v v v v v v",
                "v v v v v v v v v",
                "v v v v v v v v v",
                "v v v v v v v v v",
                ". . . . c . s . . "
            )
            .addIngredient('v', virtualInventory)
            .addIngredient('c', closeButton)
            .addIngredient('s', saveButton)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle("§8Добавление предметов")
            .setGui(gui)
            .build()
            .open()
    }
}
