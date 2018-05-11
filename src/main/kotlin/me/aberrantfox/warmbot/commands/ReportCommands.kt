package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.warmbot.ObjectRegister
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.TextChannel


@CommandSet
fun reportCommands() = commands {
    command("close") {
        execute {
            val reportService = ObjectRegister["reportService"] as ReportService

            if( !(reportService.isReportChannel(it.channel.id)) ) {
                it.respond("Nice try, but you can't close a channel that isn't a report. That would be silly. Don't do silly things.")
                return@execute
            }

            (it.channel as TextChannel).delete().queue()
        }
    }

    command("archive") {
        execute {
            it.channel.sendFile(it.channel.archiveString().toByteArray(), "$${it.channel.name}.txt").queue()
        }
    }
}