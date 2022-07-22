package me.jakejmattson.modmail.commands

import dev.kord.core.entity.interaction.GuildAutoCompleteInteraction
import me.jakejmattson.discordkt.arguments.AnyArg
import me.jakejmattson.discordkt.arguments.EveryArg
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.modmail.extensions.reactSuccess
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.MacroService

@Suppress("unused")
fun macroCommands(macroService: MacroService) = commands("Macros") {
    fun autoCompletingMacroArg() = AnyArg("Macro", "The name of a macro.").autocomplete {
        val guild = (interaction as GuildAutoCompleteInteraction).getGuild()

        macroService.getGuildMacros(guild)
            .map { it.name }
            .filter { it.contains(input, true) }
    }

    slash("AddMacro") {
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(AnyArg("Name"), EveryArg("Content")) {
            val (name, message) = args
            val wasAdded = macroService.addMacro(name, message, guild)

            if (wasAdded)
                respond("Created macro: `$name`")
            else
                respond("`$name` already exists.")
        }
    }

    slash("RemoveMacro") {
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

    slash("RenameMacro") {
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(autoCompletingMacroArg(), AnyArg("NewName")) {
            val (name, newName) = args
            val wasChanged = macroService.editName(name, newName, guild)

            if (!wasChanged)
                respond("`$newName` already exists.")
            else
                respond("Changed `$name` to `$newName`")
        }
    }

    slash("EditMacro") {
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(autoCompletingMacroArg(), EveryArg("Content")) {
            val (name, message) = args

            val wasEdited = macroService.editMessage(name, message, guild)

            if (wasEdited)
                respond("Edited macro: $name")
            else
                respond("`$name` does not exist")
        }
    }

    slash("ListMacros") {
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            respond {
                addField("Macros", macroService.listMacros(guild))
            }
        }
    }
}