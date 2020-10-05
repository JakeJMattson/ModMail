package me.jakejmattson.modmail.commands

import com.gitlab.kordlib.core.behavior.channel.*
import com.gitlab.kordlib.core.behavior.createTextChannel
import com.gitlab.kordlib.core.entity.*
import com.gitlab.kordlib.core.entity.channel.TextChannel
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.discordkt.api.extensions.addField
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import java.util.concurrent.ConcurrentHashMap

fun reportHelperCommands(configuration: Configuration,
                         reportService: ReportService,
                         moderationService: ModerationService,
                         loggingService: LoggingService) = commands("ReportHelpers") {

    suspend fun openReport(event: CommandEvent<*>, targetUser: User, guild: Guild, detain: Boolean = false) {
        val reportCategory = configuration[guild.id.longValue]!!.getLiveReportCategory(guild.kord)
        val privateChannel = targetUser.getDmChannel()

        privateChannel.createEmbed {
            if (detain)
                addField("You've have been detained by the staff of ${guild.name}!", Locale.USER_DETAIN_MESSAGE)
            else
                addField("Chatting with ${guild.name}!", Locale.BOT_DESCRIPTION)
        }

        val reportChannel = guild.createTextChannel {
            name = targetUser.username
            parentId = reportCategory?.id
        }

        reportChannel.createEmbed {
            author {
                name = targetUser.username
                icon = targetUser.avatar.url
            }

            description = targetUser.descriptor()
        }

        val newReport = Report(targetUser.id.value, reportChannel.id.value, guild.id.value, ConcurrentHashMap())
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

            openReport(this, targetMember.asUser(), guild)
        }
    }

    guildCommand("Detain") {
        description = Locale.DETAIN_DESCRIPTION
        execute(MemberArg) {
            val targetMember = args.first
            val guild = guild

            if (moderationService.hasStaffRole(targetMember)) {
                respond("You cannot detain another staff member.")
                return@execute
            }

            targetMember.mute()

            if (targetMember.isDetained()) {
                respond("This member is already detained.")
                return@execute
            }

            val user = targetMember.asUser()

            if (!hasValidState(this, guild, user))
                return@execute

            openReport(this, user, guild, true)
        }
    }

    guildCommand("Release") {
        description = Locale.RELEASE_DESCRIPTION
        execute(ChannelArg<TextChannel>("Report Channel").makeOptional { it.channel as TextChannel }, MemberArg) {
            val (inputChannel, targetMember) = args
            val report = inputChannel.toReportChannel()?.report

            if (report == null) {
                respond(createChannelError(inputChannel))
                return@execute
            }

            if (!targetMember.isDetained()) {
                respond("This member is not detained.")
                return@execute
            }

            report.release(discord.api)
            respond("${targetMember.tag} has been released.")
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