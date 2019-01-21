package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@CommandSet("report")
fun reportCommands(reportService: ReportService, configuration: Configuration, loggingService: LoggingService) = commands {

	fun openReport(event: CommandEvent, targetUser: User, message: String, guildId: String) {
		val guildConfiguration = configuration.getGuildConfig(guildId)!!
		val reportCategory = reportService.jda.getCategoryById(guildConfiguration.reportCategory)

		reportCategory.createTextChannel(targetUser.name).queue { channel ->
			channel as TextChannel

			val initialMessage =
				if (message.isNotEmpty()) {
					targetUser.sendPrivateMessage(message, DefaultLogger())
					message
				} else {
                    Locale.messages.COMMON_NO_MESSAGE
                }

			channel.sendMessage(embed {
				setColor(Color.green)
				addField("New Report Opened!",
					"${targetUser.descriptor()} :: ${targetUser.asMention}",
					false)
				addField("This report was opened by a staff member!",
					"${event.author.descriptor()} :: ${event.author.asMention}",
					false)
				addField("Initial Message", initialMessage, false)
			}).queue()

			val newReport = Report(targetUser.id, channel.id, guildId, ConcurrentHashMap())
			reportService.addReport(newReport)

			event.respond("Channel opened at: ${channel.asMention}")
			loggingService.staffOpen(newReport, event.author)
		}
	}

	command("open") {
		requiresGuild = true
		description = "Open a report with the target user and send the provided initial message."
		expect(arg(UserArg), arg(SentenceArg("Initial Message"), optional = true, default = ""))
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

			val userEmbed = embed {
				setColor(Color.green)
				setThumbnail(guild.iconUrl)
				addField("You've received a message from the staff of ${guild.name}!",
					"This is a two-way communication medium between you and the entire staff team. " +
							"Reply directly into this channel and your message will be forwarded to them.",
					false)
			}

			targetUser.openPrivateChannel().queue {
				it.sendMessage(userEmbed).queue({
					openReport(event, targetUser, message, guild.id)
				},
				{
					event.respond("Unable to contact the target user. Direct messages are disabled.")
				})
			}
		}
	}

    command("close") {
		requiresGuild = true
        description = "Close the report channel that this command is invoked in. Alternatively, delete the channel."
        execute {
            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond(
                        "Nice try, but you can't close a channel that isn't a report. That would be silly. Don't do silly things.")
                return@execute
            }

            val report = reportService.getReportByChannel(it.channel.id)
            (it.channel as TextChannel).delete().queue()
            loggingService.close(report, it.author)
        }
    }

    command("closeall") {
		requiresGuild = true
        description = "Close all currently open reports. Can be invoked in any channel."
        execute {

            val reportsFromGuild = reportService.getReportsFromGuild(it.message.guild.id)
            val author = it.author

            if (reportsFromGuild.isEmpty()) {
                it.respond("There are no reports to close.")
                return@execute
            }

            reportsFromGuild.forEach {
                reportService.jda.getTextChannelById(it.channelId).delete().queue()
                loggingService.close(it, author)
            }

            it.respond("${reportsFromGuild.size} report(s) closed successfully.")
        }
    }

    command("archive") {
		requiresGuild = true
        description = "Archive the contents of the report as a text document in the archive channel."
        execute {

            if (!(reportService.isReportChannel(it.channel.id))) {
                it.respond("You can't archive something that isn't a report...")
                return@execute
            }

            val relevantGuild = configuration.getGuildConfig(it.message.guild.id)!!
            val archiveChannel = it.jda.getTextChannelById(relevantGuild.archiveChannel)
            val targetChannel = it.jda.getTextChannelById(it.channel.id)
            val report = reportService.getReportByChannel(it.channel.id)

            archiveChannel.sendFile(it.channel.archiveString(configuration.prefix).toByteArray(),
                    "$${it.channel.name}.txt").queue {
                targetChannel.delete().queue()
            }

            loggingService.archive(report, it.author)
        }
    }
}