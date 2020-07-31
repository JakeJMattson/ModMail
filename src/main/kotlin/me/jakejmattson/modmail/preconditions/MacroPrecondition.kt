package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.command.CommandEvent
import me.jakejmattson.discordkt.api.dsl.preconditions.*
import me.jakejmattson.discordkt.api.extensions.jda.*
import me.jakejmattson.modmail.services.*

class MacroPrecondition : Precondition() {
    override fun evaluate(event: CommandEvent<*>): PreconditionResult {
        val command = event.command
        val commandName = event.rawInputs.commandName.toLowerCase()

        if (command != null) return Pass

        val macroService = event.discord.getInjectionObjects(MacroService::class)
        val macro = macroService.getGuildMacros(event.guild!!).firstOrNull { it.name.toLowerCase() == commandName }
            ?: return Fail()

        event.respond {
            val report = event.channel.toLiveReport()

            color =
                if (report != null) {
                    report.user.sendPrivateMessage(macro.message)
                    addField("Macro Sent (${event.author.fullName()})", macro.message, false)
                    successColor
                } else {
                    addField("Macro Text (Not Sent)", macro.message, false)
                    infoColor
                }
        }

        return Fail()
    }

}