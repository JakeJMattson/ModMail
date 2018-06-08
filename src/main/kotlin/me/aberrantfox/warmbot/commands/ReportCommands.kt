package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.CommandSet
import me.aberrantfox.kjdautils.api.dsl.commands
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.Configuration
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.TextChannel


@CommandSet
fun reportCommands(reportService: ReportService, configuration: Configuration) = commands {
    command("close") {
        execute {
            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond(
                        "Nice try, but you can't close a channel that isn't a report. That would be silly. Don't do silly things.")
                return@execute
            }
            (it.channel as TextChannel).delete().queue()
        }
    }

    command("archive") {
        execute {

            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond("You can't archive something that isn't a report...")
                return@execute
            }

            val relevantGuild = configuration.guildConfigurations.first { g ->
                g.guildId == reportService.getReportByChannel(it.channel.id).guildId
            }

            val archiveChannel = it.jda.getTextChannelById(relevantGuild.archiveChannel)
            val targetChannel = it.jda.getTextChannelById(it.channel.id)

            archiveChannel.sendFile(it.channel.archiveString(relevantGuild.prefix).toByteArray(), "$${it.channel.name}.txt").queue {
                (targetChannel as TextChannel).delete().queue()
            }
        }
    }
}