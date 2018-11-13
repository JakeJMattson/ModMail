package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.TextChannel

@CommandSet
fun reportCommands(configuration: Configuration, reportService: ReportService, loggingService: LoggingService) = commands {
    command("close") {
        description = "Close the report channel that this command is invoked in. Alternatively, delete the channel."
        execute {
            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond(
                        "Nice try, but you can't close a channel that isn't a report. That would be silly. Don't do silly things.")
                return@execute
            }

            val report = reportService.getReportByChannel(it.channel.id)

            reportService.sendReportClosedEmbed(reportService.getReportByChannel(it.channel.id))
            (it.channel as TextChannel).delete().queue()
            loggingService.logClose(report, it.author)
        }
    }

    command("closeall") {
        description = "Close all currently open reports. Can be invoked in any channel."
        execute {

            val reports = reportService.reports
            val currentGuild = it.message.guild.id
            val reportsFromGuild = reports.filter { it.guildId == currentGuild }
            val author = it.author

            if (reportsFromGuild.isEmpty()) {
                it.respond("There are no reports to close.")
                return@execute
            }

            var closeCount = 0

            reportsFromGuild.forEach {
                reportService.sendReportClosedEmbed(it)
                reportService.jda.getTextChannelById(it.channelId).delete().queue()
                closeCount++
                loggingService.logClose(it, author)
            }

            it.respond("$closeCount report(s) closed successfully.")
        }
    }

    command("archive") {
        description = "Archive the contents of the report as a text document in the archive channel."
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
            val report = reportService.getReportByChannel(it.channel.id)

            archiveChannel.sendFile(it.channel.archiveString(relevantGuild.prefix).toByteArray(),
                    "$${it.channel.name}.txt").queue {
                reportService.sendReportClosedEmbed(report)
                targetChannel.delete().queue()
            }

            loggingService.logArchive(report, it.author)
        }
    }
}