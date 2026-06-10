package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder
import ru.nilsson03.library.invui.gui.ScrollGui
import ru.nilsson03.library.invui.gui.structure.Markers
import ru.nilsson03.library.invui.item.Item
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.AutoUpdateItem
import ru.nilsson03.library.invui.item.impl.SuppliedItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.scrollDownButton
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.scrollUpButton
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.skullBuilder
import ru.nilsson03.treasures.model.Treasure
import java.util.UUID
import java.util.function.Supplier

object TreasureListMenu {

    fun open(player: Player) {
        val repository = TreasuresPlugin.instance.treasureRepository

        val closeButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Закрыть меню")
                        .setLore(listOf("§7Нажмите, чтобы закрыть данное", "§7меню."))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M1ZmQ0MTJiY2VjNDhmMjk4MDIyOWY0NGFkNzg1ZTlkMjQ3ZjY2YjZhOTFlZWY3YTk4ZDc2NmJkMTFkNGExOSJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            player.closeInventory()
            true
        }

        val addTreasure = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Добавить сокровищницу")
                        .setLore(listOf(
                            "",
                            "§7Используйте данную кнопку, чтобы перейти",
                            "§7К созданию новой сокровищницы.",
                            "",
                            "§8▹ §eНажмите, чтобы перейти"
                        ))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMjlmMjc3NzNmMDhkY2M2ODU0NzNhMjFmNjQ3NmZlYjZhYWQ4OWFjMjRhMTNmMDRlZjYzNjkyYjlhYzlmZWE5MCJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            val treasure = Treasure(uuid = UUID.randomUUID())
            repository.addTreasure(treasure)
            TreasureSetupMenu.open(player, treasure)
            true
        }

        val items: List<Item> = repository.getTreasures().map { treasure ->
            object : AutoUpdateItem(20, Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.CHEST)
                        .setDisplayName("§fСокровищница §e${treasure.displayName.ifEmpty { treasure.uuid.toString() }}")
                        .setLore(listOf(
                            "",
                            " §8▹ §eНажмите, чтобы перейти к настройке сокровищницы"
                        ))
                        .build()
                }
            }) {
                override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                    TreasureSetupMenu.open(player, treasure)
                }
            }
        }

        val gui = ScrollGui.items()
            .setStructure(
                "f f f f f f f f f",
                "f x x x x x x x f",
                "f x x x x x x x f",
                "f x x x x x x x f",
                "d f f f f f f f u",
                ". . . . c . a . . "
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('u', scrollUpButton())
            .addIngredient('d', scrollDownButton())
            .addIngredient('f', SpigotItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build())
            .addIngredient('c', closeButton)
            .addIngredient('a', addTreasure)
            .setContent(items)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle("§8Список сокровищниц")
            .setGui(gui)
            .build()
            .open()
    }
}
