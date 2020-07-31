package me.jakejmattson.modmail.commands

import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.extensions.jda.*
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.entities.*
import java.util.concurrent.ConcurrentHashMap

@CommandSet("ReportHelpers")
fun reportHelperCommands(configuration: Configuration, reportService: ReportService,
                         moderationService: ModerationService, loggingService: LoggingService) = commands {

    data class EmbedData(val topic: String, val openMessage: String, val initialMessage: String)

    fun openReport(event: CommandEvent<*>, targetUser: User, guild: Guild, userEmbed: MessageEmbed, embedData: EmbedData, detain: Boolean = false) {
        val guildId = guild.idLong
        val reportCategory = configuration[guildId]!!.getLiveReportCategory(guild.jda)

        targetUser.openPrivateChannel().queue {
            it.sendMessage(userEmbed).queue({
                reportCategory?.createTextChannel(targetUser.name)?.queue { channel ->
                    channel as TextChannel

                    val message = embedData.initialMessage

                    val initialMessage =
                        if (message.isNotEmpty()) {
                            targetUser.sendPrivateMessage(message)
                            message
                        } else {
                            Locale.DEFAULT_INITIAL_MESSAGE
                        }

                    val reportEmbed = embed {
                        color = if (!detain) successColor else failureColor
                        thumbnail = targetUser.effectiveAvatarUrl
                        addField(embedData.topic, targetUser.descriptor(), false)
                        addField(embedData.openMessage, event.author.descriptor(), false)
                        addField("Initial Message", initialMessage, false)
                    }

                    channel.sendMessage(reportEmbed).queue()

                    val newReport = Report(targetUser.idLong, channel.idLong, guildId, ConcurrentHashMap())
                    reportService.addReport(newReport)

                    if (detain) newReport.detain(it.jda)

                    event.respond("Success! Channel opened at: ${channel.asMention}")
                    loggingService.staffOpen(guild, channel.name, event.author)
                }
            },
                {
                    event.respond("Unable to contact the target user. Direct messages are disabled or the bot is blocked.")
                })
        }
    }

    command("Open") {
        description = Locale.OPEN_DESCRIPTION
        execute(MemberArg, EveryArg("Initial Message").makeOptional("")) { event ->
            val (targetMember, message) = event.args
            val guild = event.message.guild

            if (!hasValidState(event, guild, targetMember.user))
                return@execute

            val userEmbed = embed {
                color = successColor
                thumbnail = guild.iconUrl
                addField("Chatting with ${guild.name}!", Locale.BOT_DESCRIPTION, false)
            }

            val embedData = EmbedData("New Report Opened!", "This report was opened by", message)
            openReport(event, targetMember.user, guild, userEmbed, embedData)
        }
    }

    command("Detain") {
        description = Locale.DETAIN_DESCRIPTION
        execute(MemberArg, EveryArg("Initial Message").makeOptional("")) { event ->
            val (targetMember, message) = event.args
            val guild = event.message.guild

            if (moderationService.hasStaffRole(targetMember))
                return@execute event.respond("You cannot detain another staff member.")

            targetMember.mute()

            if (targetMember.isDetained())
                return@execute event.respond("This member is already detained.")

            if (!hasValidState(event, guild, targetMember.user))
                return@execute

            val userEmbed = embed {
                color = failureColor
                thumbnail = guild.iconUrl
                addField("You've have been detained by the staff of ${guild.name}!", Locale.USER_DETAIN_MESSAGE, false)
            }

            val embedData = EmbedData("User Detained!", "This user was detained by", message)
            openReport(event, targetMember.user, guild, userEmbed, embedData, true)
        }
    }

    command("Release") {
        description = Locale.RELEASE_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel }, MemberArg) {
            val (inputChannel, targetMember) = it.args
            val (_, report) = inputChannel.toReportChannel()
                ?: return@execute it.respond(createChannelError(inputChannel))

            if (!targetMember.isDetained())
                return@execute it.respond("This member is not detained.")

            report.release(it.discord.jda)
            it.respond("${targetMember.fullName()} has been released.")
        }
    }

    command("Info") {
        description = Locale.INFO_DESCRIPTION
        execute(TextChannelArg("Report Channel").makeOptional { it.channel as TextChannel },
            ChoiceArg("Field", "user", "channel", "all").makeOptional("user")) {

            val (inputChannel, choice) = it.args
            val report = inputChannel.toReportChannel()?.report
                ?: return@execute it.respond(createChannelError(inputChannel))

            val response = with(report) {
                when (choice) {
                    "user" -> userId.toString()
                    "channel" -> channelId.toString()
                    "all" -> "User ID: $userId\nChannel ID: $channelId"
                    else -> "Invalid selection!"
                }
            }

            inputChannel.sendMessage(response)
        }
    }

    command("History") {
        description = Locale.HISTORY_DESCRIPTION
        execute(UserArg) {
            val user = it.args.first
            val channel = it.channel

            val privateChannel = user.openPrivateChannel().complete()
                ?: return@execute it.respond("Unable to establish private channel. Direct messages are disabled or the bot is blocked.")

            val history = privateChannel.archiveString().toByteArray()

            if (history.isEmpty())
                return@execute it.respond("No history available.")

            channel.sendFile(history, "$${user.id}.txt").queue()
        }
    }
}

private fun hasValidState(event: CommandEvent<*>, currentGuild: Guild, targetUser: User): Boolean {
    val report = targetUser.toLiveReport() ?: return true
    val reportGuild = report.guild

    event.respond("The target user already has an open report " +
        if (reportGuild == currentGuild) {
            val channel = report.channel.asMention
            "at $channel."
        } else {
            "in ${reportGuild.name}."
        }
    )

    return false
}