package me.jakejmattson.modmail.commands

import dev.kord.common.kColor
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.util.*
import me.jakejmattson.modmail.services.Locale
import me.jakejmattson.modmail.services.MacroService
import me.jakejmattson.modmail.services.findReport
import java.awt.Color

@Suppress("unused")
fun macroCommands(macroService: MacroService) = subcommand("Macro") {
    fun autoCompletingMacroArg() = AnyArg("Macro", "The name of a macro").autocomplete {
        val guild = (interaction as GuildAutoCompleteInteraction).getGuild()

        macroService.getGuildMacros(guild)
            .map { it.name }
            .filter { it.contains(input, true) }
    }

    sub("Send", Locale.SEND_MACRO_DESCRIPTION) {
        execute(autoCompletingMacroArg()) {
            val name = args.first
            val macro = macroService.findMacro(guild, name)
            val report = channel.findReport()

            if (macro == null) {
                respond("`$name` does not exist.")
                return@execute
            }

            if (report != null)
                report.liveMember(discord.kord)?.sendPrivateMessage(macro.message)

            respondPublic {
                description = macro.message
                color = if (report != null) Color.green.kColor else Color.red.kColor
                footer("Macro: ${macro.name}")
            }
        }
    }

    sub("Add", Locale.ADD_MACRO_DESCRIPTION) {
        execute(AnyArg("Name", "The name used to reference this macro"),
            EveryArg("Content", "The content displayed when this macro is sent")) {
            val (name, message) = args
            val wasAdded = macroService.addMacro(name, message, guild)

            if (wasAdded)
                respondPublic("Created macro: `$name`")
            else
                respond("`$name` already exists.")
        }
    }

    sub("Remove", Locale.REMOVE_MACRO_DESCRIPTION) {
        execute(autoCompletingMacroArg()) {
            val name = args.first
            val wasRemoved = macroService.removeMacro(name, guild)

            if (wasRemoved)
                respondPublic("Deleted macro: `${name}`")
            else
                respond("`${name}` does not exist.")
        }
    }

    sub("Rename", Locale.RENAME_MACRO_DESCRIPTION) {
        execute(autoCompletingMacroArg(), AnyArg("NewName", "The new name to give this macro")) {
            val (name, newName) = args
            val wasChanged = macroService.editName(name, newName, guild)

            if (wasChanged)
                respondPublic("Changed `$name` to `$newName`")
            else
                respond("`$newName` already exists.")
        }
    }

    sub("Edit", Locale.EDIT_MACRO_DESCRIPTION) {
        execute(autoCompletingMacroArg(),
            EveryArg("Content", "The new content of the macro")) {
            val (name, message) = args

            val wasEdited = macroService.editMessage(name, message, guild)

            if (wasEdited)
                respondPublic("Edited macro: $name")
            else
                respond("`$name` does not exist")
        }
    }

    sub("List", Locale.LIST_MACROS_DESCRIPTION) {
        execute {
            respond {
                addField("Macros", macroService.listMacros(guild))
            }
        }
    }
}