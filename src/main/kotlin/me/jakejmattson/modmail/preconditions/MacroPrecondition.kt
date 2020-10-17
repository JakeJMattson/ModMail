package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.precondition
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.services.*

fun macroPrecondition() = precondition {
    val commandName = rawInputs.commandName.toLowerCase()

    if (command != null) return@precondition

    val macroService = discord.getInjectionObjects(MacroService::class)
    val macro = macroService.getGuildMacros(guild!!).firstOrNull { it.name.toLowerCase() == commandName }
        ?: return@precondition fail()

    respond {
        val report = channel.toLiveReport()

        if (report != null) {
            report.user.sendPrivateMessage(macro.message)
            addField("Macro Sent (${this@precondition.author.tag})", macro.message)
        } else {
            addField("Macro Text (Not Sent)", macro.message)
        }
    }

    fail()
}