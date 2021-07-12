package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.AnyArg
import me.jakejmattson.discordkt.api.arguments.EveryArg
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.modmail.arguments.MacroArg
import me.jakejmattson.modmail.extensions.reactSuccess
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.MacroService

@Suppress("unused")
fun macroCommands(macroService: MacroService) = commands("Macros") {
    guildCommand("AddMacro") {
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(AnyArg("Name"), EveryArg("Content")) {
            val (name, message) = args
            val wasAdded = macroService.addMacro(name, message, guild)

            if (!wasAdded)
                respond("`$name` already exists.")
            else
                reactSuccess()
        }
    }

    guildCommand("RemoveMacro") {
        description = Locale.REMOVE_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = args.first
            macroService.removeMacro(macro, guild)
            reactSuccess()
        }
    }

    guildCommand("RenameMacro") {
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(MacroArg, AnyArg("NewName")) {
            val (macro, newName) = args
            val wasChanged = macroService.editName(macro, newName, guild)

            if (!wasChanged)
                respond("`$newName` already exists.")
            else
                reactSuccess()
        }
    }

    guildCommand("EditMacro") {
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(MacroArg, EveryArg("Content")) {
            val (macro, message) = args
            macroService.editMessage(macro, message)
            reactSuccess()
        }
    }

    guildCommand("ListMacros") {
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            respond {
                addField("Macros", macroService.listMacros(guild))
            }
        }
    }
}