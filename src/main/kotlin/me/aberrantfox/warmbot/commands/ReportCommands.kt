package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.warmbot.ObjectRegister
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.Configuration
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
            val config = ObjectRegister["config"] as Configuration
            val reportService = ObjectRegister["reportService"] as ReportService
            val archiveChannel = it.jda.getTextChannelById(config.archiveChannel)
            val targetChannel = it.channel

            if( !(reportService.isReportChannel(it.channel.id)) ) {
                it.respond("You can't archive something that isn't a report...")
                return@execute
            }

            archiveChannel.sendFile(it.channel.archiveString().toByteArray(), "$${it.channel.name}.txt").queue {
                (targetChannel as TextChannel).delete().queue()
            }
        }
    }
}