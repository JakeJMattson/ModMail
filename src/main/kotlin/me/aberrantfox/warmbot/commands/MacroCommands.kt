package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.command.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.warmbot.arguments.MacroArg
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import java.awt.Color

@CommandSet("Macros")
fun macroCommands(macroService: MacroService) = commands {
    command("SendMacro") {
        requiresGuild = true
        description = Locale.SEND_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = it.args.first

            it.respond {
                color =
                    if (it.channel.isReportChannel()) {
                        it.channel.channelToReport().reportToUser()?.sendPrivateMessage(macro.message)
                        addField("Macro sent by ${it.author.fullName()}!", macro.message, false)
                        Color.green
                    } else {
                        addField("Macro not sent - must be invoked within a report channel!", macro.message, false)
                        Color.red
                    }
            }
        }
    }

    command("AddMacro") {
        requiresGuild = true
        description = Locale.ADD_MACRO_DESCRIPTION
        execute(WordArg("Macro Name"), SentenceArg("Macro Content")) {
            val (name, message) = it.args
            val response = macroService.addMacro(name, message, it.guild!!)
            it.respond(response.second)
        }
    }

    command("RemoveMacro") {
        requiresGuild = true
        description = Locale.REMOVE_MACRO_DESCRIPTION
        execute(MacroArg) {
            val macro = it.args.first
            val response = macroService.removeMacro(macro, it.guild!!)
            it.respond(response.second)
        }
    }

    command("RenameMacro") {
        requiresGuild = true
        description = Locale.RENAME_MACRO_DESCRIPTION
        execute(MacroArg, WordArg("New Name")) {
            val (macro, newName) = it.args
            val response = macroService.editName(macro, newName, it.guild!!)
            it.respond(response.second)
        }
    }

    command("EditMacro") {
        requiresGuild = true
        description = Locale.EDIT_MACRO_DESCRIPTION
        execute(MacroArg, SentenceArg("New Message")) {
            val (macro, message) = it.args
            val response = macroService.editMessage(macro, message)
            it.respond(response.second)
        }
    }

    command("ListMacros") {
        requiresGuild = true
        description = Locale.LIST_MACROS_DESCRIPTION
        execute {
            it.respond {
                addField("Currently Available Macros", macroService.listMacros(it.guild!!), false)
                color = Color.GREEN
            }
        }
    }
}