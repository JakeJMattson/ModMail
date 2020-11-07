package me.jakejmattson.modmail.preconditions

import me.jakejmattson.discordkt.api.dsl.precondition
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.services.*
import java.awt.Color

fun macroPrecondition() = precondition {
    val commandName = rawInputs.commandName.toLowerCase()

    if (command != null) return@precondition

    val macroService = discord.getInjectionObjects(MacroService::class)
    val macro = macroService.getGuildMacros(guild!!).firstOrNull { it.name.toLowerCase() == commandName }
        ?: return@precondition fail()

    message.delete()

    respond {
        val report = channel.toLiveReport()

        description = macro.message

        color = if (report != null) {
            report.user.sendPrivateMessage(macro.message)

            author {
                name = this@precondition.author.tag
                icon = this@precondition.author.avatar.url
            }

            Color.green
        } else {
            Color.red
        }

        footer {
            text = "Macro: ${macro.name}"
        }
    }

    fail()
}