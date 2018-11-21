package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.archiveString
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@CommandSet
fun reportCommands(reportService: ReportService, configuration: Configuration) = commands {
	command("open") {
		description = "Open a report with the target user and send the provided initial message."
		expect(arg(UserArg), arg(SentenceArg("Initial Message"), optional = true, default = ""))
		execute { event ->
			val targetUser = event.args.component1() as User
			var message = event.args.component2() as String
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

					val guildConfiguration = configuration.guildConfigurations.first { g -> g.guildId == guild.id }
					val reportCategory = reportService.jda.getCategoryById(guildConfiguration.reportCategory)

					reportCategory.createTextChannel(targetUser.name).queue { channel ->
						channel as TextChannel

						var initialMessage = "<No initial message provided>"

						if (message.isNotEmpty()) {
							initialMessage = message
							targetUser.sendPrivateMessage(message, DefaultLogger())
						}

						channel.sendMessage(
							embed {
								addField("New Report Opened!",
									"${targetUser.descriptor()} :: ${targetUser.asMention}",
									false)
								addField("This report was opened by a staff member!",
									"${event.author.descriptor()} :: ${event.author.asMention}",
									false)
								addField("Initial Message", initialMessage, false)
								setColor(Color.green)
							}).queue()

						val newReport = Report(targetUser.id, channel.id, guild.id, ConcurrentHashMap(), null)
						reportService.addReport(newReport)

						event.respond("Channel opened at: ${channel.asMention}")
					}
				},
				{
					event.respond("Unable to contact the target user. Direct messages are disabled.")
				})
			}
		}
	}

    command("close") {
        description = "Close the report channel that this command is invoked in. Alternatively, delete the channel."
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
        description = "Close all currently open reports. Can be invoked in any channel."
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
            val reportChannel = reportService.getReportByChannel(it.channel.id)

            archiveChannel.sendFile(it.channel.archiveString(relevantGuild.prefix).toByteArray(),
                    "$${it.channel.name}.txt").queue {
                reportService.sendReportClosedEmbed(reportChannel)
                targetChannel.delete().queue()
            }
        }
    }
}