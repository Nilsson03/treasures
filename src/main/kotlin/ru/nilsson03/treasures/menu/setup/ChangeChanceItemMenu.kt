package ru.nilsson03.treasures.menu.setup

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import ru.nilsson03.library.bukkit.item.builder.impl.SpigotItemBuilder
import ru.nilsson03.library.invui.gui.Gui
import ru.nilsson03.library.invui.item.ItemProvider
import ru.nilsson03.library.invui.item.impl.AutoUpdateItem
import ru.nilsson03.library.invui.item.impl.SuppliedItem
import ru.nilsson03.library.invui.window.Window
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.model.Treasure
import java.util.concurrent.atomic.AtomicReference
import java.util.function.Supplier

object ChangeChanceItemMenu {

    fun open(player: Player, treasure: Treasure, itemStack: ItemStack, currentChance: Double) {
        val percent = AtomicReference(currentChance)

        val info = AutoUpdateItem(20, Supplier {
            ItemProvider { _ ->
                SpigotItemBuilder(itemStack)
                    .addLine("")
                    .addLine("§8▪ §fТекущая вероятность: §e${"%.2f".format(percent.get())}%")
                    .build()
            }
        })

        val add1 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aДобавить 1%")
                        .setLore(listOf("", "§7Нажмите, чтобы добавить §f1%", "§7к вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { it + 1.0 }
            true
        }

        val add10 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aДобавить 10%")
                        .setLore(listOf("", "§7Нажмите, чтобы добавить §f10%", "§7к вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { it + 10.0 }
            true
        }

        val add50 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aДобавить 50%")
                        .setLore(listOf("", "§7Нажмите, чтобы добавить §f50%", "§7к вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { it + 50.0 }
            true
        }

        val remove1 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName("§cУбавить на 1%")
                        .setLore(listOf("", "§7Нажмите, чтобы убавить на §f1%", "§7вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { v -> (v - 1.0).coerceAtLeast(0.1) }
            true
        }

        val remove10 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName("§cУбавить на 10%")
                        .setLore(listOf("", "§7Нажмите, чтобы убавить на §f10%", "§7вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { v -> (v - 10.0).coerceAtLeast(0.1) }
            true
        }

        val remove50 = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.RED_STAINED_GLASS_PANE)
                        .setDisplayName("§cУбавить на 50%")
                        .setLore(listOf("", "§7Нажмите, чтобы убавить на §f50%", "§7вероятности выпадения"))
                        .build()
                }
            }
        ) { _ ->
            percent.updateAndGet { v -> (v - 50.0).coerceAtLeast(0.1) }
            true
        }

        val delete = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.BARRIER)
                        .setDisplayName("§cУдалить предмет")
                        .setLore(listOf("", "§7Нажмите, чтобы удалить предмет"))
                        .build()
                }
            }
        ) { _ ->
            treasure.items.remove(itemStack)
            TreasureListItemsMenu.open(player, treasure)
            true
        }

        val save = SuppliedItem(
            Supplier {
                ItemProvider { _ ->
                    SpigotItemBuilder(Material.LIME_STAINED_GLASS_PANE)
                        .setDisplayName("§aСохранить")
                        .setLore(listOf("", "§7Нажмите, чтобы сохранить вероятность"))
                        .build()
                }
            }
        ) { _ ->
            treasure.items[itemStack] = percent.get()
            TreasuresPlugin.instance.treasureRepository.save()
            UniversalMessenger.send(player, "§a✔ §fВероятность сохранена: §e${"%.2f".format(percent.get())}%")
            TreasureListItemsMenu.open(player, treasure)
            true
        }

        val gui = Gui.normal()
            .setStructure(
                ". . . . . . . . .",
                ". q w e i r t y .",
                ". . . . . . . . .",
                ". . d . s . . . ."
            )
            .addIngredient('q', add1)
            .addIngredient('w', add10)
            .addIngredient('e', add50)
            .addIngredient('r', remove50)
            .addIngredient('t', remove10)
            .addIngredient('y', remove1)
            .addIngredient('i', info)
            .addIngredient('d', delete)
            .addIngredient('s', save)
            .build()

        Window.single()
            .setViewer(player)
            .setTitle("§8Настройка вероятности предмета")
            .setGui(gui)
            .build()
            .open()
    }
}
