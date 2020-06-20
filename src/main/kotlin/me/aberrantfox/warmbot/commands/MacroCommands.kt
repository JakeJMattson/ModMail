package me.aberrantfox.warmbot.commands

import me.aberrantfox.warmbot.arguments.MacroArg
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.commands
import me.jakejmattson.kutils.api.extensions.jda.*

@CommandSet("Macros")
fun macroCommands(macroService: MacroService) = commands {
    command("SendMacro") {
        description = Locale.SEND_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = it.args.first

            it.respond {
                val report = it.channel.channelToReport()

                color =
                    if (report != null) {
                        report.reportToUser()?.sendPrivateMessage(macro.message)
                        addField("Macro sent by ${it.author.fullName()}!", macro.message, false)
                        successColor
                    } else {
                        addField("Macro not sent - must be invoked within a report channel!", macro.message, false)
                        failureColor
                    }
            }
        }
    }

    command("AddMacro") {
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(AnyArg("Macro Name"), EveryArg("Macro Content")) {
            val (name, message) = it.args
            val response = macroService.addMacro(name, message, it.guild!!)
            it.respond(response.second)
        }
    }

    command("RemoveMacro") {
        description = Locale.REMOVE_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = it.args.first
            val response = macroService.removeMacro(macro, it.guild!!)
            it.respond(response.second)
        }
    }

    command("RenameMacro") {
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(MacroArg, AnyArg("New Name")) {
            val (macro, newName) = it.args
            val response = macroService.editName(macro, newName, it.guild!!)
            it.respond(response.second)
        }
    }

    command("EditMacro") {
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(MacroArg, EveryArg("New Message")) {
            val (macro, message) = it.args
            val response = macroService.editMessage(macro, message)
            it.respond(response.second)
        }
    }

    command("ListMacros") {
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            it.respond {
                addField("Currently Available Macros", macroService.listMacros(it.guild!!), false)
                color = infoColor
            }
        }
    }
}