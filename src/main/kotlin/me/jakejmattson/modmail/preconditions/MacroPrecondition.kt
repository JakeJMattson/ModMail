package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.services.*

class MacroPrecondition : Precondition() {
    override suspend fun evaluate(event: CommandEvent): PreconditionResult {
        val commandName = event.rawInputs.commandName.toLowerCase()

        if (event.command != null) return Pass

        val macroService = event.discord.getInjectionObjects(MacroService::class)
        val macro = macroService.getGuildMacros(event.guild!!).firstOrNull { it.name.toLowerCase() == commandName }
            ?: return Fail()

        event.respond {
            val report = event.channel.toLiveReport()

            if (report != null) {
                report.user.sendPrivateMessage(macro.message)
                addField("Macro Sent (${event.author.tag})", macro.message)
            } else {
                addField("Macro Text (Not Sent)", macro.message)
            }
        }

        return Fail()
    }
}