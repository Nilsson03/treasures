package ru.nilsson03.treasures.command

import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player
import ru.nilsson03.library.text.messeger.UniversalMessenger
import ru.nilsson03.library.text.util.ReplaceData
import ru.nilsson03.treasures.TreasuresPlugin
import ru.nilsson03.treasures.block.TreasureBlockManager
import ru.nilsson03.treasures.file.MessagesFile
import ru.nilsson03.treasures.manager.KeyManager
import ru.nilsson03.treasures.service.TreasureService
import java.util.UUID

class TreasuresCommand(
    private val treasureService: TreasureService,
    private val blockManager: TreasureBlockManager,
    private val keyManager: KeyManager
) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("treasures.admin")) return false

        if (args.isEmpty()) {
            UniversalMessenger.send(sender, MessagesFile.getList("messages.commands_help"))
            return true
        }

        when (args[0].uppercase()) {
            "SET-BLOCK" -> handleSetBlock(sender, args)
            "GIVE" -> handleGive(sender, args)
            "RELOAD" -> handleReload(sender)
            else -> UniversalMessenger.send(sender, MessagesFile.getList("messages.commands_help"))
        }
        return true
    }

    private fun handleSetBlock(sender: CommandSender, args: Array<out String>) {
        if (sender !is Player) return

        if (args.size != 2) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_set_block_usage"))
            return
        }

        val block = sender.getTargetBlockExact(10)
        if (block == null) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_set_block_block_is_null"))
            return
        }

        val uuid = try { UUID.fromString(args[1]) } catch (e: Exception) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_wrong_value", ReplaceData("{value}", args[1])))
            return
        }

        val treasure = TreasuresPlugin.instance.treasureRepository.getTreasureByUuid(uuid)
        if (treasure == null) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_treasure_not_found"))
            return
        }

        blockManager.setTreasureBlock(sender, treasure, block)
    }

    private fun handleGive(sender: CommandSender, args: Array<out String>) {
        if (args.size != 4) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_give_usage"))
            return
        }

        val target = Bukkit.getPlayer(args[1])
        if (target == null) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_give_player_is_null"))
            return
        }

        val uuid = try { UUID.fromString(args[2]) } catch (e: Exception) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_wrong_value", ReplaceData("{value}", args[2])))
            return
        }

        val treasure = TreasuresPlugin.instance.treasureRepository.getTreasureByUuid(uuid)
        if (treasure == null) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_treasure_not_found"))
            return
        }

        val amount = try { args[3].toInt() } catch (e: Exception) {
            UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_wrong_value", ReplaceData("{value}", args[3])))
            return
        }

        keyManager.giveKeys(target.uniqueId, treasure, amount)
        UniversalMessenger.send(
            target,
            MessagesFile.getString(
                "messages.treasure_received_key",
                ReplaceData("{treasure}", treasure.displayName),
                ReplaceData("{amount}", amount)
            )
        )
        UniversalMessenger.send(
            sender,
            MessagesFile.getString(
                "messages.commands_give_give",
                ReplaceData("{amount}", amount),
                ReplaceData("{player}", target.name)
            )
        )
    }

    private fun handleReload(sender: CommandSender) {
        val plugin = TreasuresPlugin.instance
        plugin.reloadConfig()
        ru.nilsson03.treasures.file.Config.load()
        MessagesFile.load()
        ru.nilsson03.treasures.file.InventoriesFile.load()
        ru.nilsson03.treasures.file.TranslationsFile.load()
        plugin.treasureRepository.load()
        UniversalMessenger.send(sender, MessagesFile.getString("messages.commands_reload"))
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (!sender.hasPermission("treasures.admin")) return emptyList()

        return when (args.size) {
            1 -> listOf("set-block", "give", "reload").filter { it.startsWith(args[0], ignoreCase = true) }
            2 -> when (args[0].lowercase()) {
                "give" -> Bukkit.getOnlinePlayers().map { it.name }.filter { it.startsWith(args[1], ignoreCase = true) }
                "set-block" -> TreasuresPlugin.instance.treasureRepository.getTreasures().map { it.uuid.toString() }
                else -> emptyList()
            }
            3 -> if (args[0].equals("give", ignoreCase = true)) {
                TreasuresPlugin.instance.treasureRepository.getTreasures().map { it.uuid.toString() }
            } else emptyList()
            else -> emptyList()
        }
    }
}
