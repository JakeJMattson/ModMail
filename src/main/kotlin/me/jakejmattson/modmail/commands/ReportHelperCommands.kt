package me.jakejmattson.modmail.commands

import dev.kord.common.exception.RequestException
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.channel.createMessage
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.entity.Guild
import dev.kord.core.entity.Member
import dev.kord.core.entity.User
import dev.kord.core.entity.channel.TextChannel
import dev.kord.rest.Image
import me.jakejmattson.discordkt.arguments.ChoiceArg
import me.jakejmattson.discordkt.arguments.UserArg
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.extensions.addField
import me.jakejmattson.modmail.arguments.ReportChannelArg
import me.jakejmattson.modmail.extensions.archiveString
import me.jakejmattson.modmail.locale.Locale
import me.jakejmattson.modmail.services.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
fun reportHelperCommands(configuration: Configuration, reportService: ReportService, loggingService: LoggingService) = commands("ReportHelpers") {

    suspend fun Member.openReport(event: CommandEvent<*>, detain: Boolean = false) {
        val guild = guild.asGuild()
        val reportCategory = configuration[guild]!!.getLiveReportCategory(guild.kord)

        getDmChannel().createEmbed {
            if (detain) {
                color = Color.green.kColor
                addField("You've have been detained by the staff of ${guild.name}!", Locale.USER_DETAIN_MESSAGE)
            } else {
                color = Color.red.kColor
                addField("Chatting with ${guild.name}!", Locale.BOT_DESCRIPTION)
            }

            thumbnail {
                url = guild.getIconUrl(Image.Format.JPEG) ?: ""
            }
        }

        val reportChannel = guild.createTextChannel(username) {
            parentId = reportCategory?.id
        }

        reportOpenEmbed(reportChannel, detain)

        val newReport = Report(id, reportChannel.id, guild.id, ConcurrentHashMap())
        reportService.addReport(newReport)

        if (detain) newReport.detain(guild.kord)

        event.respond(reportChannel.mention)
        loggingService.staffOpen(guild, (event.channel as TextChannel).name, event.author)
    }

    slash("Open", "Open a Report") {
        description = Locale.OPEN_DESCRIPTION
        execute(UserArg) {
            val user = args.first
            val targetMember = user.asMemberOrNull(guild.id)

            if (targetMember == null) {
                println("User is no longer in this guild.")
                return@execute
            }

            if (!hasValidState(this, guild, targetMember))
                return@execute

            try {
                targetMember.openReport(this, false)
            } catch (ex: RequestException) {
                respond("Unable to contact the target user. Direct messages are disabled or the bot is blocked.")
                return@execute
            }
        }
    }

    slash("Detain", "Detain this User") {
        description = Locale.DETAIN_DESCRIPTION
        execute(UserArg) {
            val user = args.first
            val targetMember = user.asMemberOrNull(guild.id)

            if (targetMember == null) {
                println("User is no longer in this guild.")
                return@execute
            }

            if (targetMember.getPermissions().contains(discord.configuration.defaultPermissions)) {
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

    slash("Release") {
        description = Locale.RELEASE_DESCRIPTION
        execute(implicitReportChannel()) {
            val report = args.first?.report

            if (report == null) {
                respond("Invalid report channel")
                return@execute
            }

            val member = guild.getMemberOrNull(report.userId)

            if (member == null) {
                respond("This user is not in the server.")
                return@execute
            }

            if (!member.isDetained()) {
                respond("This member is not detained.")
                return@execute
            }

            report.release(discord.kord)
            respond("${member.tag} has been released.")
        }
    }

    slash("Info") {
        description = Locale.INFO_DESCRIPTION
        execute(implicitReportChannel(), ChoiceArg("Field", "The info to display", "user", "channel", "all").optional("user")) {
            val (reportChannel, choice) = args

            if (reportChannel == null) {
                respond("Invalid report channel")
                return@execute
            }

            val (channel, report) = reportChannel

            val response = with(report) {
                when (choice) {
                    "user" -> userId.toString()
                    "channel" -> channelId.toString()
                    "all" -> "User ID: $userId\nChannel ID: $channelId"
                    else -> "Invalid selection!"
                }
            }

            channel.createMessage(response)
        }
    }

    slash("History", "View Report History") {
        description = Locale.HISTORY_DESCRIPTION
        execute(UserArg) {
            val user = args.first
            val history = user.getDmChannel().archiveString().toByteArray()

            if (history.isEmpty()) {
                respond("No history available.")
                return@execute
            }

            channel.createMessage {
                addFile("$${user.id.value}.txt", history.inputStream())
            }
        }
    }
}

private suspend fun hasValidState(event: CommandEvent<*>, currentGuild: Guild, targetUser: User): Boolean {
    val report = targetUser.toLiveReport() ?: return true
    val reportGuild = report.guild

    event.respond(
        if (reportGuild == currentGuild) {
            report.channel.mention
        } else {
            "The target user already has an open report in ${reportGuild.name}."
        }
    )

    return false
}