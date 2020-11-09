package me.jakejmattson.modmail.commands

import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.commands
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.modmail.arguments.MacroArg
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.MacroService
import java.awt.Color

fun macroCommands(macroService: MacroService) = commands("Macros") {
    guildCommand("AddMacro") {
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(AnyArg("Macro Name"), EveryArg("Macro Content")) {
            val (name, message) = args
            val response = macroService.addMacro(name, message, guild)

            respond {
                description = message

                color = if (response) Color.GREEN else Color.RED

                footer {
                    text = if (response) "Macro added: $name" else "Macro already exists: $name"
                }
            }
        }
    }

    guildCommand("RemoveMacro") {
        description = Locale.REMOVE_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = args.first
            val response = macroService.removeMacro(macro, guild)

            respond {
                description = macro.message

                color = Color.RED

                footer {
                    text = "Macro removed: ${macro.name}"
                }
            }
        }
    }

    guildCommand("RenameMacro") {
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(MacroArg, AnyArg("New Name")) {
            val (macro, newName) = args
            val response = macroService.editName(macro, newName, guild)
            respond(response.second)
        }
    }

    guildCommand("EditMacro") {
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(MacroArg, EveryArg("New Message")) {
            val (macro, message) = args
            val response = macroService.editMessage(macro, message)
            respond(response.second)
        }
    }

    guildCommand("ListMacros") {
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            respond {
                addField("Currently Available Macros", macroService.listMacros(guild))
            }
        }
    }
}