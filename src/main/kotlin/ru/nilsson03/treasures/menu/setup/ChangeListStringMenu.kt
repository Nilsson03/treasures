package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.nilsson03.library.invui.gui.Gui
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.builder.ItemBuilder
import ru.nilsson03.library.invui.item.impl.AbstractItem
import ru.nilsson03.library.invui.item.impl.AutoUpdateItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.api.UniversalTextApi
import ru.nilsson03.treasures.util.ChatInputHandler
import java.util.concurrent.atomic.AtomicInteger

object ChangeListStringMenu {

    fun open(
        player: Player,
        list: MutableList<String>,
        onSave: (MutableList<String>) -> Unit,
        onBack: () -> Unit
    ) {
        openMenu(player, "&8Редактор списка", list, onSave, onBack)
    }

    fun openMenu(
        player: Player,
        title: String,
        list: MutableList<String>,
        onSave: (MutableList<String>) -> Unit,
        onClose: () -> Unit
    ) {
        val currentIndex = AtomicInteger(0)

        val infoItem = object : AutoUpdateItem(20, {
            ItemBuilder(Material.BOOK)
                .setDisplayName(UniversalTextApi.colorize("&6Текущий список"))
                .addLoreLines(
                    UniversalTextApi.colorize(""),
                    UniversalTextApi.colorize(" &8▪ &fТекущие элементы:"),
                    UniversalTextApi.colorize(""),
                    *getColoredList(list, currentIndex.get()).toTypedArray(),
                    UniversalTextApi.colorize(""),
                    UniversalTextApi.colorize("&8▹ &eНажмите SHIFT+ЛКМ, чтобы редактировать строку"),
                    UniversalTextApi.colorize("&8▹ &eНажмите ПКМ, чтобы перейти к следующей строке"),
                    UniversalTextApi.colorize("&8▹ &eНажмите ЛКМ, чтобы удалить строку")
                )
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                event.isCancelled = true
                when (clickType) {
                    ClickType.LEFT -> {
                        if (list.isNotEmpty()) {
                            list.removeAt(currentIndex.get())
                            if (list.isNotEmpty()) {
                                currentIndex.set(currentIndex.get().coerceAtMost(list.size - 1))
                            }
                        }
                    }
                    ClickType.RIGHT -> {
                        if (list.isNotEmpty()) {
                            currentIndex.set((currentIndex.get() + 1) % list.size)
                        }
                    }
                    ClickType.SHIFT_LEFT -> {
                        if (list.isNotEmpty()) {
                            player.closeInventory()
                            ChatInputHandler.requestInput(
                                player,
                                "&eВведите новое значение:"
                            ) { input ->
                                list[currentIndex.get()] = input
                                openMenu(player, title, list, onSave, onClose)
                            }
                        }
                    }
                    else -> {}
                }
            }
        }

        val addItem = object : AbstractItem() {
            override fun getItemProvider(): ItemProvider {
                return ItemBuilder(Material.PAPER)
                    .setDisplayName(UniversalTextApi.colorize("&6Добавить строку"))
                    .addLoreLines(
                        UniversalTextApi.colorize(""),
                        UniversalTextApi.colorize("&fИспользуйте данную кнопку для добавления"),
                        UniversalTextApi.colorize("&fстроки в список"),
                        UniversalTextApi.colorize(""),
                        UniversalTextApi.colorize("&8▹ &eНажмите, чтобы добавить")
                    )
            }

            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                event.isCancelled = true
                if (clickType == ClickType.LEFT) {
                    player.closeInventory()
                    ChatInputHandler.requestInput(
                        player,
                        "&eВведите новое значение:"
                    ) { input ->
                        list.add(input)
                        openMenu(player, title, list, onSave, onClose)
                    }
                }
            }
        }

        val saveItem = object : AbstractItem() {
            override fun getItemProvider(): ItemProvider {
                return ItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                    .setDisplayName(UniversalTextApi.colorize("&6Сохранить список"))
                    .addLoreLines(
                        UniversalTextApi.colorize(""),
                        UniversalTextApi.colorize("&7Нажмите, чтобы сохранить настройки"),
                        UniversalTextApi.colorize("&7списка."),
                        UniversalTextApi.colorize(""),
                        UniversalTextApi.colorize(" &8▹ &eНажмите, чтобы сохранить")
                    )
            }

            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                event.isCancelled = true
                if (clickType == ClickType.LEFT) {
                    onSave(list)
                    player.closeInventory()
                    onClose()
                }
            }
        }

        val backItem = object : AbstractItem() {
            override fun getItemProvider(): ItemProvider {
                return ItemBuilder(Material.RED_STAINED_GLASS_PANE)
                    .setDisplayName(UniversalTextApi.colorize("&6Назад"))
                    .addLoreLines(
                        UniversalTextApi.colorize(""),
                        UniversalTextApi.colorize("&8▹ &eНажмите для возврата без сохранения")
                    )
            }

            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                event.isCancelled = true
                if (clickType == ClickType.LEFT) {
                    player.closeInventory()
                    onClose()
                }
            }
        }

        val gui = Gui.normal()
            .setStructure(
                "# # # # i # # # #",
                "# . . . . . . . #",
                "# # a # b # s # #",
                "# . . . . . . . #",
                "# # # # # # # # #"
            )
            .addIngredient('#', BorderItem())
            .addIngredient('i', infoItem)
            .addIngredient('a', addItem)
            .addIngredient('b', backItem)
            .addIngredient('s', saveItem)
            .build()

        val window = Window.single()
            .setViewer(player)
            .setTitle(UniversalTextApi.colorize(title))
            .setGui(gui)
            .build()

        window.open()
    }

    private fun getColoredList(list: List<String>, selectedIndex: Int): List<String> {
        return list.mapIndexed { index, value ->
            if (index == selectedIndex) {
                UniversalTextApi.colorize("&e▸ &f$value")
            } else {
                UniversalTextApi.colorize("&7  $value")
            }
        }
    }

    private class BorderItem : AbstractItem() {
        override fun getItemProvider(): ItemProvider {
            return ItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ")
        }

        override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
            event.isCancelled = true
        }
    }
}
