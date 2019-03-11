package me.aberrantfox.warmbot.commands

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.extensions.jda.*
import me.aberrantfox.kjdautils.internal.command.arguments.*
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.entities.*
import net.dv8tion.jda.core.managers.ChannelManager
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@CommandSet("report")
fun reportCommands(configuration: Configuration, loggingService: LoggingService) = commands {
    command("Close") {
        requiresGuild = true
        description = Locale.messages.CLOSE_DESCRIPTION
        execute {
            val channel = it.channel as TextChannel

            channel.delete().queue()
            loggingService.close(it.guild!!.id, channel.name, it.author)
        }
    }

    command("Archive") {
        requiresGuild = true
        description = Locale.messages.ARCHIVE_DESCRIPTION
        expect(arg(SentenceArg("Additional Info"), optional = true, default = ""))
        execute {
            val relevantGuild = configuration.getGuildConfig(it.message.guild.id)!!
            val archiveChannel = relevantGuild.archiveChannel.idToTextChannel()
            val channel = it.channel.id.idToTextChannel()

            val note = it.args.component1() as String

            if (note.isNotEmpty())
                archiveChannel.sendMessage(note).queue()

            archiveChannel.sendFile(it.channel.archiveString(configuration.prefix).toByteArray(),
                "$${it.channel.name}.txt").queue {
                channel.delete().queue()
            }

            loggingService.archive(it.guild!!.id, channel.name, it.author)
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

    command("Move") {
        requiresGuild = true
        description = "Move a report from the current category to another category."
        expect(ChannelCategoryArg("Category ID"))
        execute {
            val channel = it.channel as Channel
            val oldCategory = channel.parent
            val newCategory = it.args.component1() as Category

            ChannelManager(channel).setParent(newCategory).queue()
            it.message.delete().queue()

            it.respond(
                embed {
                    field {
                        name = "Report Moved!"
                        value = "This report was moved from `${oldCategory.name}` to `${newCategory.name}` by ${it.author.fullName()}"
                        inline = false
                    }
                    color(Color.ORANGE)
                }
            )
        }
    }
}

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
        description = "Retrieve the requested id info from the target report channel. Fields: user, channel, guild."
        expect(TextChannelArg, ChoiceArg("Field", "user", "channel", "guild"))
        execute {
            val channel = it.args.component1() as TextChannel
            val choice = it.args.component2() as String

            if (!channel.isReportChannel())
                return@execute it.respond("Target channel must be a report channel.")

            val report = channel.channelToReport()

            it.respond(
                with (report) {
                    when (choice) {
                        "user" -> userId
                        "channel" -> channelId
                        "guild" -> guildId
                        else -> "Invalid selection!"
                    }
                }
            )
        }
    }
}