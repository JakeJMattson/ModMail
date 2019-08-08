package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.warmbot.arguments.MacroArg
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import java.awt.Color

@CommandSet("Macros")
fun macroCommands(macroService: MacroService) = commands {
    command("SendMacro") {
        requiresGuild = true
        description = Locale.messages.SEND_MACRO_DESCRIPTION
        expect(MacroArg)
        execute {
            val macro = it.args.component1() as Macro

            it.respond(embed {
                if (it.channel.isReportChannel()) {
                    it.channel.channelToReport().reportToUser()?.sendPrivateMessage(macro.message)
                    addField("Macro sent by ${it.author.fullName()}!", macro.message, false)
                    setColor(Color.green)
                }
                else {
                    addField("Macro not sent - must be invoked within a report channel!", macro.message, false)
                    setColor(Color.red)
                }
            })
        }
    }

    command("AddMacro") {
        requiresGuild = true
        description = Locale.messages.ADD_MACRO_DESCRIPTION
        expect(WordArg("Macro Name"), SentenceArg("Macro Content"))
        execute {
            val name = it.args.component1() as String
            val message = it.args.component2() as String

            val response = macroService.addMacro(name, message, it.guild!!)

            it.respond(response.second)
        }
    }

    command("RemoveMacro") {
        requiresGuild = true
        description = Locale.messages.REMOVE_MACRO_DESCRIPTION
        expect(MacroArg)
        execute {
            val macro = it.args.component1() as Macro

            val response = macroService.removeMacro(macro, it.guild!!)

            it.respond(response.second)
        }
    }

    command("RenameMacro") {
        requiresGuild = true
        description = Locale.messages.RENAME_MACRO_DESCRIPTION
        expect(MacroArg, WordArg("New Name"))
        execute {
            val macro = it.args.component1() as Macro
            val newName = it.args.component2() as String

            val response = macroService.editName(macro, newName, it.guild!!)

            it.respond(response.second)
        }
    }

    command("EditMacro") {
        requiresGuild = true
        description = Locale.messages.EDIT_MACRO_DESCRIPTION
        expect(MacroArg, SentenceArg("New Message"))
        execute {
            val macro = it.args.component1() as Macro
            val message = it.args.component2() as String

            val response = macroService.editMessage(macro, message)

            it.respond(response.second)
        }
    }

    command("ListMacros") {
        requiresGuild = true
        description = Locale.messages.LIST_MACROS_DESCRIPTION
        execute {
            it.respond(
                embed {
                    addField("Currently Available Macros", macroService.listMacros(it.guild!!), false)
                    setColor(Color.GREEN)
                }
            )
        }
    }
}