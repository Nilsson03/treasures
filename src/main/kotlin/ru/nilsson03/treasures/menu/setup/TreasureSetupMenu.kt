package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.inventory.ClickType
import org.bukkit.event.inventory.InventoryClickEvent
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder
import ru.nilsson03.library.invui.gui.Gui
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.AutoUpdateItem
import ru.nilsson03.library.invui.item.impl.SuppliedItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.menu.setup.SetupMenuUtil.skullBuilder
import ru.nilsson03.treasures.model.Treasure
import ru.nilsson03.treasures.util.ChatInputHandler
import java.util.function.Supplier

object TreasureSetupMenu {

    fun open(player: Player, treasure: Treasure) {
        val closeButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Назад к списку")
                        .setLore(listOf("§7Нажмите, чтобы вернуться"))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvN2M1ZmQ0MTJiY2VjNDhmMjk4MDIyOWY0NGFkNzg1ZTlkMjQ3ZjY2YjZhOTFlZWY3YTk4ZDc2NmJkMTFkNGExOSJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            TreasureListMenu.open(player)
            true
        }

        val listItems = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    skullBuilder()
                        .setDisplayName("§6Список предметов")
                        .setLore(listOf(
                            "",
                            "§7В данном меню вы можете посмотреть предметы,",
                            "§7которые будут выпадать в сокровищнице",
                            "",
                            "§8▹ §eНажмите, чтобы посмотреть"
                        ))
                        .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMWZmMDQxOTc2YTA5ZGQwNTNlM2QxZDRlNjExYWFjMDk1OTRkNzRmYzcxYTBlYzRkYTAxMTA0MTZkMzE3ZGJhOCJ9fX0=")
                        .build()
                }
            }
        ) { _ ->
            TreasureListItemsMenu.open(player, treasure)
            true
        }

        val lore = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                val loreText = if (treasure.lore.isEmpty()) "§cНет"
                else treasure.lore.joinToString("\n") { "  §8◦ §f$it" }
                skullBuilder()
                    .setDisplayName("§6Установить текст описания")
                    .setLore(listOf(
                        "",
                        "§7С помощью данной настройки вы можете установить",
                        "§7текст описания, которое отображается в меню",
                        "",
                        " §8▪ §fТекущий текст: ",
                        "§a$loreText",
                        "",
                        "§8▹ §eНажмите, чтобы установить"
                    ))
                    .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvODdlZTRlNDg5NWE5YzZlMjQzMzZlMjQ2NjVjNTYzODI3MmEzZmVlYWM5NTU4ODJmZjkyYWUzMjE1YWU3ZiJ9fX0=")
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                ChangeListStringMenu.open(
                    player,
                    treasure.lore.toMutableList(),
                    onSave = { newList -> treasure.lore = newList },
                    onBack = { open(player, treasure) }
                )
            }
        }

        val displayName = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                skullBuilder()
                    .setDisplayName("§6Изменить отображаемое название")
                    .setLore(listOf(
                        "",
                        "§7Данная функция позволит вам изменить",
                        "§7отображаемое название для данной сокровищницы",
                        "",
                        "§8▪ §fТекущее название: ${treasure.displayName.ifEmpty { "§cНет" }}",
                        "",
                        "§8▹ §eНажмите, чтобы изменить"
                    ))
                    .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvMmFkOGEzYTNiMzZhZGQ1ZDk1NDFhOGVjOTcwOTk2ZmJkY2RlYTk0MTRjZDc1NGM1MGU0OGU1ZDM0ZjFiZjYwYSJ9fX0=")
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                UniversalMessenger.send(player, "§6➤ §fВведите, пожалуйста, новое отображаемое название:")
                player.closeInventory()
                ChatInputHandler.addPlayer(player) { input ->
                    treasure.displayName = input
                    open(player, treasure)
                }
            }
        }

        val displayItem = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                val currentType = treasure.displayInventoryItem?.type?.name ?: "§cНет"
                SpigotItemBuilder(Material.TRIPWIRE_HOOK)
                    .setDisplayName("§6Изменить отображаемый в меню предмет")
                    .setLore(listOf(
                        "",
                        "§7Данная функция позволит вам изменить",
                        "§7предмет, который будет отображаться в меню",
                        "§7сокровищницы",
                        "",
                        " §8▪ §fТекущий предмет: $currentType",
                        "",
                        "§8▹ §eВозьмите предмет в руку и напишите §6установить"
                    ))
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                UniversalMessenger.send(player, "§6➤ §fВозьмите нужный предмет в руку и напишите в чат §6§lустановить")
                player.closeInventory()
                ChatInputHandler.addPlayer(player) { input ->
                    if (input.equals("установить", ignoreCase = true)) {
                        val handItem = player.inventory.itemInMainHand
                        if (handItem.type == Material.AIR) {
                            UniversalMessenger.send(player, "§6➤ §cВы не держите предмет в руке!")
                            open(player, treasure)
                            return@addPlayer
                        }
                        treasure.displayInventoryItem = handItem.clone()
                    }
                    open(player, treasure)
                }
            }
        }

        val reward = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                skullBuilder()
                    .setDisplayName("§6Количество открытий для получения награды")
                    .setLore(listOf(
                        "",
                        "§7Данная функция позволит вам изменить",
                        "§7через сколько открытий игроку будет",
                        "§7выдана награда",
                        "",
                        "§8▪ §fТекущее значение: ${treasure.needOpensToReward}",
                        "",
                        "§8▹ §eНажмите, чтобы изменить"
                    ))
                    .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYmY3NWQxYjc4NWQxOGQ0N2IzZWE4ZjBhN2UwZmQ0YTFmYWU5ZTdkMzIzY2YzYjEzOGM4Yzc4Y2ZlMjRlZTU5In19fQ==")
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                UniversalMessenger.send(player, "§6➤ §fВведите, пожалуйста, новое количество открытий:")
                player.closeInventory()
                ChatInputHandler.addPlayer(player) { input ->
                    try {
                        treasure.needOpensToReward = input.toInt()
                    } catch (e: NumberFormatException) {
                        UniversalMessenger.send(player, "§6➤ §cВы ввели неверное значение: $input")
                    }
                    open(player, treasure)
                }
            }
        }

        val rewards = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                val commandsText = if (treasure.rewardCommands.isEmpty()) "§cНет"
                else treasure.rewardCommands.joinToString("\n") { "  §8◦ §f$it" }
                skullBuilder()
                    .setDisplayName("§6Изменить награды за открытие")
                    .setLore(listOf(
                        "",
                        "§7С помощью данной настройки вы можете установить",
                        "§7награды, которые будут выданы игроку при достижении",
                        "§7необходимого количества открытий",
                        "",
                        " §8▪ §fТекущие награды: ",
                        "§a$commandsText",
                        "",
                        "§8▹ §eНажмите, чтобы установить"
                    ))
                    .setSkinTexture("eyJ0ZXh0dXJlcyI6eyJTS0lOIjp7InVybCI6Imh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvYWYyMmI2YTNhMGYyNGJkZWVhYjJhNmFjZDliMWY1MmJiOTU5NGQ1ZjZiMWUyYzA1ZGRkYjIxOTQxMGM4In19fQ==")
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                ChangeListStringMenu.open(
                    player,
                    treasure.rewardCommands.toMutableList(),
                    onSave = { newList -> treasure.rewardCommands = newList },
                    onBack = { open(player, treasure) }
                )
            }
        }

        val hologram = object : AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                val hologramText = if (treasure.hologramLines.isEmpty()) "§cНет"
                else treasure.hologramLines.joinToString("\n") { "  §8◦ §f$it" }
                SpigotItemBuilder(Material.PAPER)
                    .setDisplayName("§6Изменить текст голограммы")
                    .setLore(listOf(
                        "",
                        "§7С помощью данной настройки вы можете установить",
                        "§7текст голограммы, которая отображается над сокровищницей",
                        "",
                        " §8▪ §fТекущий текст: ",
                        "§a$hologramText",
                        "",
                        "§8▹ §eНажмите, чтобы установить"
                    ))
                    .build()
            }
        }) {
            override fun handleClick(clickType: ClickType, player: Player, event: InventoryClickEvent) {
                ChangeListStringMenu.open(
                    player,
                    treasure.hologramLines.toMutableList(),
                    onSave = { newList -> treasure.hologramLines = newList },
                    onBack = { open(player, treasure) }
                )
            }
        }

        val saveButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aСохранить сокровищницу")
                        .setLore(listOf("", "§7Нажмите, чтобы сохранить все изменения"))
                        .build()
                }
            }
        ) { _ ->
            TreasuresPlugin.instance.treasureRepository.save()
            UniversalMessenger.send(player, "§a✔ §fСокровищница сохранена")
            true
        }

        val deleteButton = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName("§cУдалить сокровищницу")
                        .setLore(listOf("", "§7Нажмите, чтобы удалить"))
                        .build()
                }
            }
        ) { _ ->
            TreasuresPlugin.instance.treasureRepository.removeTreasure(treasure)
            TreasuresPlugin.instance.treasureRepository.save()
            UniversalMessenger.send(player, "§c✕ §fСокровищница удалена")
            TreasureListMenu.open(player)
            true
        }

        val gui = Gui.normal()
            .setStructure(
                "b b b b b b b b b",
                "b q . w . e . . b",
                "b t . y . u . i b",
                "b . . . . . . . b",
                "b b b b b b b b b",
                "d . . s c . . x . "
            )
            .addIngredient('c', closeButton)
            .addIngredient('q', listItems)
            .addIngredient('w', displayName)
            .addIngredient('e', lore)
            .addIngredient('t', displayItem)
            .addIngredient('y', reward)
            .addIngredient('u', rewards)
            .addIngredient('i', hologram)
            .addIngredient('s', saveButton)
            .addIngredient('d', deleteButton)
            .addIngredient('x', SpigotItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build())
            .addIngredient('b', SpigotItemBuilder(Material.GRAY_STAINED_GLASS_PANE).setDisplayName(" ").build())
            .build()

        Window.single()
            .setViewer(player)
            .setTitle("§8Настройка сокровищницы")
            .setGui(gui)
            .build()
            .open()
    }
}
