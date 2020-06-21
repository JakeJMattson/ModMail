package me.aberrantfox.warmbot.preconditions

import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.annotations.Precondition
import me.jakejmattson.kutils.api.dsl.preconditions.*
import me.jakejmattson.kutils.api.extensions.jda.*

@Precondition
fun macroPrecondition() = precondition {
    val command = it.command
    val commandName = it.rawInputs.commandName.toLowerCase()

    if (command != null) return@precondition Pass

    val macroService = it.discord.getInjectionObjects(MacroService::class)
    val macro = macroService.getGuildMacros(it.guild!!).firstOrNull { it.name.toLowerCase() == commandName }
        ?: return@precondition Fail()

    it.respond {
        val report = it.channel.channelToReport()

        color =
            if (report != null) {
                report.reportToUser()?.sendPrivateMessage(macro.message)
                addField("Macro Sent (${it.author.fullName()})", macro.message, false)
                successColor
            } else {
                addField("Macro Text (Not Sent)", macro.message, false)
                infoColor
            }
    }

    return@precondition Fail()
}