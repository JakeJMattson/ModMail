package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.common.exception.RequestException
import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

fun reportHelperCommands(configuration: Configuration,
                         reportService: ReportService,
                         moderationService: ModerationService,
                         loggingService: LoggingService) = commands("ReportHelpers") {

    suspend fun Member.openReport(event: CommandEvent<*>, detain: Boolean = false) {
        val guild = guild.asGuild()
        val reportCategory = configuration[guild.id.longValue]!!.getLiveReportCategory(guild.kord)
        val privateChannel = getDmChannel()

        privateChannel.createEmbed {
            if (detain) {
                color = Color.green
                addField("You've have been detained by the staff of ${guild.name}!", Locale.USER_DETAIN_MESSAGE)
            } else {
                color = Color.red
                addField("Chatting with ${guild.name}!", Locale.BOT_DESCRIPTION)
            }
        }

        val reportChannel = guild.createTextChannel {
            name = username
            parentId = reportCategory?.id
        }

        reportChannel.createEmbed {
            author {
                name = username
                icon = avatar.url
            }

            description = descriptor()
        }

        val newReport = Report(id.value, reportChannel.id.value, guild.id.value, ConcurrentHashMap())
        reportService.addReport(newReport)

        if (detain) newReport.detain(guild.kord)

        event.respond("Success! Channel opened at: ${reportChannel.mention}")
        loggingService.staffOpen(guild, (event.channel as TextChannel).name, event.author)
    }

    guildCommand("Open") {
        description = Locale.OPEN_DESCRIPTION
        execute(MemberArg) {
            val targetMember = args.first

            if (!hasValidState(this, guild, targetMember))
                return@execute

            try {
                targetMember.openReport(this)
            } catch (ex: RequestException) {
                respond("Unable to contact the target user. " +
                        "Direct messages are disabled or the bot is blocked.")

                return@execute
            }
        }
    }

    guildCommand("Detain") {
        description = Locale.DETAIN_DESCRIPTION
        execute(MemberArg) {
            val targetMember = args.first

            if (moderationService.hasStaffRole(targetMember)) {
                respond("You cannot detain another staff member.")
                return@execute
            }

            if (targetMember.isDetained()) {
                respond("This member is already detained.")
                return@execute
            }

            if (!hasValidState(this, guild, targetMember))
                return@execute

            try {
                targetMember.openReport(this, true)
            } catch (ex: RequestException) {
                respond("Unable to contact the target user. " +
                        "Direct messages are disabled or the bot is blocked. " +
                        "Mute was not applied")

                return@execute
            }

            targetMember.mute()
        }
    }

    guildCommand("Release") {
        description = Locale.RELEASE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }) {
            val (inputChannel) = args
            val report = inputChannel.toReportChannel()?.report

            if (report == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

            val member = guild.getMemberOrNull(report.userId.toSnowflake())

            if (member == null) {
                respond("This user is not in the server.")
                return@execute
            }

            if (!member.isDetained()) {
                respond("This member is not detained.")
                return@execute
            }

            report.release(discord.api)
            respond("${member.tag} has been released.")
        }
    }

    guildCommand("Info") {
        description = Locale.INFO_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel },
            ChoiceArg("Field", "user", "channel", "all").makeOptional("user")) {

            val (inputChannel, choice) = args
            val report = inputChannel.toReportChannel()?.report

            if (report == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

            val response = with(report) {
                when (choice) {
                    "user" -> userId
                    "channel" -> channelId
                    "all" -> "User ID: $userId\nChannel ID: $channelId"
                    else -> "Invalid selection!"
                }
            }

            inputChannel.createMessage(response)
        }
    }

    guildCommand("History") {
        description = Locale.HISTORY_DESCRIPTION
        execute(UserArg) {
            val user = args.first
            val history = user.getDmChannel().archiveString().toByteArray()

            if (history.isEmpty()) {
                respond("No history available.")
                return@execute
            }

            channel.createMessage {
                addFile("$${user.id}.txt", history.inputStream())
            }
        }
    }
}

private suspend fun hasValidState(event: CommandEvent<*>, currentGuild: Guild, targetUser: User): Boolean {
    val report = targetUser.toLiveReport() ?: return true
    val reportGuild = report.guild

    event.respond("The target user already has an open report " +
        if (reportGuild == currentGuild) {
            val channel = report.channel.mention
            "at $channel."
        } else {
            "in ${reportGuild.name}."
        }
    )

    return false
}