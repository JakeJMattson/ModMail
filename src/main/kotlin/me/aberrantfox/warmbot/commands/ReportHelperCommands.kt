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
    fun openReport(event: CommandEvent, targetUser: User, guildId: String, userEmbed: MessageEmbed, reportEmbed: MessageEmbed, detain: Boolean = false) {
        val reportCategory = configuration.getGuildConfig(guildId)!!.reportCategory.idToCategory()

        targetUser.openPrivateChannel().queue {
            it.sendMessage(userEmbed).queue({
                reportCategory.createTextChannel(targetUser.name).queue { channel ->
                    channel as TextChannel

                    channel.sendMessage(reportEmbed).queue()

                    val newReport = Report(targetUser.id, channel.id, guildId, ConcurrentHashMap())
                    reportService.addReport(newReport)

                    if (detain) newReport.detain()

                    event.respond("Success! Channel opened at: ${channel.asMention}")
                    loggingService.staffOpen(guildId, channel.name, event.author)
                }
            },
            {
                event.respond("Unable to contact the target user. Direct messages are disabled.")
            })
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

            if (!hasValidState(event, guild, targetUser))
                return@execute

            val userEmbed = embed {
                setColor(Color.green)
                setThumbnail(guild.iconUrl)
                addField("You've received a message from the staff of ${guild.name}!", Locale.messages.BOT_DESCRIPTION, false)
            }

            val initialMessage =
                if (message.isNotEmpty()) {
                    targetUser.sendPrivateMessage(message, DefaultLogger())
                    message
                } else {
                    Locale.messages.DEFAULT_INITIAL_MESSAGE
                }

            val reportMessage = embed {
                setColor(Color.green)
                setThumbnail(targetUser.avatarUrl)
                addField("New Report Opened!",
                    "${targetUser.descriptor()} :: ${targetUser.asMention}",
                    false)
                addField("This report was opened by a staff member!",
                    "${event.author.descriptor()} :: ${event.author.asMention}",
                    false)
                addField("Initial Message", initialMessage, false)
            }

            openReport(event, targetUser, guild.id, userEmbed, reportMessage, true)
        }
    }

    command("Detain") {
        requiresGuild = true
        description = Locale.messages.DETAIN_DESCRIPTION
        expect(UserArg)
        execute { event ->
            val targetUser = event.args.component1() as User
            val guild = event.message.guild

            if (targetUser.isBot) return@execute event.respond("The target user is a bot.")
            if (!guild.isMember(targetUser)) return@execute event.respond("The target user is not in this guild.")

            if (!hasValidState(event, guild, targetUser))
                return@execute

            val mutedRole = guild.getRolesByName("Muted", true).firstOrNull()
                ?: return@execute event.respond("Guild missing `Muted` role!")

            val member = targetUser.toMember(guild)

            if (member.roles.contains(mutedRole))
                return@execute event.respond("Muted members cannot be detained. Please use `open` instead.")

            val userEmbed = embed {
                setColor(Color.red)
                setThumbnail(guild.iconUrl)

                addField("You've have been detained by the staff of ${guild.name}!",
                    "",//TODO add locale message
                    false)
            }

            val reportMessage = embed {
                setColor(Color.red)
                setThumbnail(targetUser.avatarUrl)
                addField("User Detained!",
                    "${targetUser.descriptor()} :: ${targetUser.asMention}",
                    false)
                addField("This user was detained by",
                    "${event.author.descriptor()} :: ${event.author.asMention}",
                    false)
            }

            openReport(event, targetUser, guild.id, userEmbed, reportMessage, true)
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

private fun hasValidState(event: CommandEvent, currentGuild: Guild, targetUser: User): Boolean {
    if (!targetUser.hasReportChannel())
        return true

    val report = targetUser.userToReport()
    val reportGuild = report.guildId.idToGuild()
    
    event.respond("The target user already has an open report " +
        if (reportGuild == currentGuild) {
            val channel = targetUser.userToReport().reportToChannel().asMention
            "at $channel."
        } else {
            "in ${reportGuild.name}."
        }
    )

    return false
}