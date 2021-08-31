package me.jakejmattson.modmail.preconditions

import dev.kord.common.kColor
import me.jakejmattson.discordkt.api.dsl.precondition
import me.jakejmattson.discordkt.api.extensions.sendPrivateMessage
import me.jakejmattson.modmail.services.MacroService
import me.jakejmattson.modmail.services.toLiveReport
import java.awt.Color
import java.util.*

@Suppress("unused")
fun macroPrecondition() = precondition {
    val commandName = rawInputs.commandName.lowercase(Locale.getDefault())

    if (command != null) return@precondition

    val macroService = discord.getInjectionObjects(MacroService::class)
    val macro = macroService.getGuildMacros(guild!!).firstOrNull { it.name.lowercase() == commandName }
        ?: return@precondition fail()

    message?.delete()

    respond {
        val report = channel.toLiveReport()

        description = macro.message

        color = if (report != null) {
            report.user.sendPrivateMessage(macro.message)

            author {
                name = this@precondition.author.tag
                icon = this@precondition.author.avatar.url
            }

            Color.green.kColor
        } else {
            Color.red.kColor
        }

        footer {
            text = "Macro: ${macro.name}"
        }
    }

    fail()
}