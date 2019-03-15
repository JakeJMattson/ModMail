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

@CommandSet("ReportHelpers")
fun reportHelperCommands(reportService: ReportService, configuration: Configuration, loggingService: LoggingService) = commands {
    fun openReport(event: CommandEvent, targetUser: User, message: String, guildId: String) {
        val reportCategory = configuration.getGuildConfig(guildId)!!.reportCategory.idToCategory()

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
                setThumbnail(targetUser.avatarUrl)
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
            loggingService.staffOpen(guildId, channel.name, event.author)
        }
    }

    command("Open") {
        requiresGuild = true
        description = Locale.messages.OPEN_DESCRIPTION
        expect(arg(UserArg), arg(SentenceArg("Initial Message"), optional = true))
        execute { event ->
            val targetUser = event.args.component1() as User
            val message = event.args.component2() as String
            val guild = event.message.guild

            if (targetUser.isBot) return@execute event.respond("The target user is a bot.")

            if (!guild.isMember(targetUser)) return@execute event.respond("The target user is not in this guild.")

            if (targetUser.hasReportChannel()) return@execute event.respond("The target user already has an open report.")

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

            if (reportsFromGuild.isEmpty()) return@execute it.respond("There are no reports to close.")

            reportsFromGuild.forEach { report ->
                val channel = report.channelId.idToTextChannel()

                channel.delete().queue()
                loggingService.close(it.guild!!.id, channel.name, it.author)
            }

            it.respond("${reportsFromGuild.size} report(s) closed successfully.")
        }
    }

    command("Info") {
        requiresGuild = true
        description = Locale.messages.INFO_DESCRIPTION
        expect(arg(TextChannelArg("Report Channel"), optional = true, default = { it.channel }),
            arg(ChoiceArg("Field", "user", "channel", "guild", "all"), optional = true, default = "all"))
        execute {
            val targetChannel = it.args.component1() as TextChannel
            val choice = it.args.component2() as String
            val error = "Command should be invoked in a report channel or target a report channel."

            if (!targetChannel.isReportChannel()) return@execute it.respond(error)

            val report = targetChannel.channelToReport()

            with(report) {
                val allData =
                    "User ID: $userId\n" +
                    "Channel ID: $channelId\n" +
                    "Guild ID: $guildId"

                it.respond(
                    when (choice) {
                        "user" -> userId
                        "channel" -> channelId
                        "guild" -> guildId
                        "all" -> allData
                        else -> "Invalid selection!"
                    }
                )
            }
        }
    }
}