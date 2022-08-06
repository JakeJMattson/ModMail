package me.jakejmattson.modmail.commands

import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.subcommand
import me.jakejmattson.discordkt.extensions.*
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

    sub("Send") {
        description = "Send a macro to a user through a report."
        execute(autoCompletingMacroArg()) {
            val name = args.first
            val macro = macroService.findMacro(guild, name)
            val report = channel.findReport()

            if (macro == null) {
                respond("`$name` does not exist.")
                return@execute
            }

            if (report != null) {
                report.liveMember(discord.kord)?.sendPrivateMessage(macro.message)
                respond("Macro content sent to user.")
            } else
                respond("Macro preview shown below.")

            channel.createEmbed {
                description = macro.message
                color = if (report != null) Color.green.kColor else Color.red.kColor
                author(this@execute.author)
                footer("Macro: ${macro.name}")
            }
        }
    }

    sub("Add") {
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(AnyArg("Name", "The name used to reference this macro"),
            EveryArg("Content", "The content displayed when this macro is sent")) {
            val (name, message) = args
            val wasAdded = macroService.addMacro(name, message, guild)

            if (wasAdded)
                respond("Created macro: `$name`")
            else
                respond("`$name` already exists.")
        }
    }

    sub("Remove") {
        description = Locale.REMOVE_MACRO_DESCRIPTION
        execute(autoCompletingMacroArg()) {
            val name = args.first
            val wasRemoved = macroService.removeMacro(name, guild)

            if (wasRemoved)
                respond("Deleted macro: `${name}`")
            else
                respond("`${name}` does not exist.")
        }
    }

    sub("Rename") {
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(autoCompletingMacroArg(), AnyArg("NewName", "The new name to give this macro")) {
            val (name, newName) = args
            val wasChanged = macroService.editName(name, newName, guild)

            if (!wasChanged)
                respond("`$newName` already exists.")
            else
                respond("Changed `$name` to `$newName`")
        }
    }

    sub("Edit") {
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(autoCompletingMacroArg(),
            EveryArg("Content", "The new content of the macro")) {
            val (name, message) = args

            val wasEdited = macroService.editMessage(name, message, guild)

            if (wasEdited)
                respond("Edited macro: $name")
            else
                respond("`$name` does not exist")
        }
    }

    sub("List") {
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            respond {
                addField("Macros", macroService.listMacros(guild))
            }
        }
    }
}