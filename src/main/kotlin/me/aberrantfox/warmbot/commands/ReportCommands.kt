package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@CommandSet("report")
fun reportCommands(reportService: ReportService, configuration: Configuration, loggingService: LoggingService) = commands {

    command("Close") {
		requiresGuild = true
        description = Locale.messages.CLOSE_DESCRIPTION
        execute {
            val report = reportService.getReportByChannel(it.channel.id)
            (it.channel as TextChannel).delete().queue()
            loggingService.close(report, it.author)
        }
    }

    command("Archive") {
		requiresGuild = true
        description = Locale.messages.ARCHIVE_DESCRIPTION
        execute {
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

	command("Note") {
		requiresGuild = true
		description = Locale.messages.NOTE_DESCRIPTION
		expect(SentenceArg)
		execute {
			it.respond(
				embed {
					field {
						name = "New note added by ${it.author.fullName()} (${it.author.id})"
						value = it.args.component1() as String
						inline = false
					}
					color(Color.ORANGE)
				}
			)

			it.message.delete().queue()
		}
	}
}

@CommandSet("ReportHelpers")
fun reportHelperCommands(reportService: ReportService, configuration: Configuration, loggingService: LoggingService) = commands {

	fun openReport(event: CommandEvent, targetUser: User, message: String, guildId: String) {
		val guildConfiguration = configuration.getGuildConfig(guildId)!!
		val reportCategory = guildConfiguration.reportCategory.idToCategory()

		reportCategory.createTextChannel(targetUser.name).queue { channel ->
			channel as TextChannel

			val initialMessage =
					if (message.isNotEmpty()) {
						targetUser.sendPrivateMessage(message, DefaultLogger())
						message
					} else {
						Locale.messages.DEFAULT_INITIAL_MESSAGE
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

	command("Open") {
		requiresGuild = true
		description = Locale.messages.OPEN_DESCRIPTION
		expect(arg(UserArg), arg(SentenceArg("Initial Message"), optional = true, default = ""))
		execute { event ->
			val targetUser = event.args.component1() as User
			val message = event.args.component2() as String
			val guild = event.message.guild
			val hasReport = reportService.hasReportChannel(targetUser.id)

			if (targetUser.isBot) return@execute event.respond("The target user is a bot.")

			if (!guild.isMember(targetUser)) return@execute event.respond("The target user is not in this guild.")

			if (hasReport) return@execute event.respond("The target user already has an open report.")

			val userEmbed = embed {
				setColor(Color.green)
				setThumbnail(guild.iconUrl)
				addField("You've received a message from the staff of ${guild.name}!", Locale.messages.BOT_DESCRIPTION, false)
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

	command("CloseAll") {
		requiresGuild = true
		description = Locale.messages.CLOSE_ALL_DESCRIPTION
		execute {
			val reportsFromGuild = reportService.getReportsFromGuild(it.message.guild.id)

			if (reportsFromGuild.isEmpty()) {
				it.respond("There are no reports to close.")
				return@execute
			}

			reportsFromGuild.forEach {report ->
				report.channelId.idToTextChannel().delete().queue()
				loggingService.close(report, it.author)
			}

			it.respond("${reportsFromGuild.size} report(s) closed successfully.")
		}
	}
}