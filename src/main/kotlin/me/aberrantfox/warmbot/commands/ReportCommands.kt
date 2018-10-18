package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color

@CommandSet
fun reportCommands(reportService: ReportService, configuration: Configuration) = commands {
    command("open") {
        expect(arg(UserArg), arg(SentenceArg))
        execute { event ->

            val targetUser = event.args.component1() as User
            val message = event.args.component2() as String
            val guild = event.message.guild

            if (targetUser.isBot) {
                event.respond("The target user is a bot.")
                return@execute
            }

            if (!guild.isMember(targetUser)) {
                event.respond("The target user is not in this guild.")
                return@execute
            }

            if (reportService.hasReportChannel(targetUser.id)) {
                event.respond("The target user already has an open report.")
                return@execute
            }

            targetUser.openPrivateChannel().queue {
                if (!targetUser.hasPrivateChannel()) {
                    event.respond("Unable to contact the target user. DM's may be disabled.")
                    return@queue
                }

                event.respond("Channel opened!")

                val embed = embed{
                    setColor(Color.green)
                    setThumbnail(guild.iconUrl)
                    addField("You've received a message from the staff of ${guild.name}!",
                        "This is a two-way communication medium between you and the entire staff team. " +
                                "Reply directly into this channel and your message will be forwarded to them.",
                        false)
                }

                targetUser.sendPrivateMessage(embed, DefaultLogger())
                targetUser.sendPrivateMessage(message, DefaultLogger())
            }
        }
    }

    command("close") {
        execute {
            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond(
                        "Nice try, but you can't close a channel that isn't a report. That would be silly. Don't do silly things.")
                return@execute
            }
            reportService.sendReportClosedEmbed(reportService.getReportByChannel(it.channel.id))
            (it.channel as TextChannel).delete().queue()
        }
    }

    command("closeall") {
        execute {

            val reports = reportService.reports
            val currentGuild = it.message.guild.id
            val reportsFromGuild = reports.filter { it.guildId == currentGuild }

            if (reportsFromGuild.isEmpty()) {
                it.respond("There are no reports to close.")
                return@execute
            }

            var closeCount = 0

            reportsFromGuild.forEach {
                reportService.sendReportClosedEmbed(it)
                reportService.jda.getTextChannelById(it.channelId).delete().queue()
                closeCount++
            }

            it.respond("$closeCount report(s) closed successfully.")
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
            val reportChannel = reportService.getReportByChannel(it.channel.id)

            archiveChannel.sendFile(it.channel.archiveString(relevantGuild.prefix).toByteArray(),
                    "$${it.channel.name}.txt").queue {
                reportService.sendReportClosedEmbed(reportChannel)
                targetChannel.delete().queue()
            }
        }
    }
}