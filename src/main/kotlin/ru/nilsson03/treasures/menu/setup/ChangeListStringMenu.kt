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
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.scrollDownButton
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.scrollUpButton
import ru.nilsson03.treasures.util.ChatInputHandler
import java.util.function.Supplier

object ChangeListStringMenu {

    fun open(
        player: Player,
        lines: MutableList<String>,
        onSave: (MutableList<String>) -> Unit,
        onBack: () -> Unit
    ) {
        val workingList = lines.toMutableList()

        val closeButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.BARRIER)
                        .setDisplayName("§6Назад")
                        .setLore(listOf("", "§7Нажмите, чтобы вернуться"))
                        .build()
                }
            }
        ) { _ ->
            onBack()
            true
        }

        val saveButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aСохранить")
                        .setLore(listOf("", "§7Нажмите, чтобы сохранить"))
                        .build()
                }
            }
        ) { _ ->
            onSave(workingList)
            onBack()
            true
        }

        val addButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§6Добавить строку")
                        .setLore(listOf("", "§7Напишите строку в чат"))
                        .build()
                }
            }
        ) { _ ->
            UniversalMessenger.send(player, "§6➤ §fВведите строку в чат:")
            player.closeInventory()
            ChatInputHandler.addPlayer(player) { input ->
                workingList.add(input)
                open(player, workingList, onSave, onBack)
            }
            true
        }

        val items: List<Item> = workingList.mapIndexed { index, line ->
            object : AutoUpdateItem(20, Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.PAPER)
                        .setDisplayName("§f$line")
                        .setLore(listOf("", " §8▪ §fСтрока №${index + 1}", "", "§8▹ §eНажмите, чтобы удалить"))
                        .build()
                }
            }) {
                override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                    workingList.removeAt(index)
                    open(player, workingList, onSave, onBack)
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
                ". a . . s . . c . "
            )
            .addIngredient('x', Markers.CONTENT_LIST_SLOT_HORIZONTAL)
            .addIngredient('u', scrollUpButton())
            .addIngredient('d', scrollDownButton())
            .addIngredient('f', SpigotItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build())
            .addIngredient('c', closeButton)
            .addIngredient('s', saveButton)
            .addIngredient('a', addButton)
            .setContent(items)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle("§8Редактирование списка строк")
            .setGui(gui)
            .build()
            .open()
    }
}
