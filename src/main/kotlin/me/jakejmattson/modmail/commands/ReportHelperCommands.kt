package me.jakejmattson.modmail.commands

import dev.kord.common.exception.RequestException
import dev.kord.common.kColor
import dev.kord.core.behavior.channel.createEmbed
import dev.kord.core.behavior.createTextChannel
import dev.kord.core.behavior.interaction.respondPublic
import dev.kord.core.entity.Member
import dev.kord.rest.Image
import io.ktor.client.request.forms.*
import io.ktor.utils.io.jvm.javaio.*
import me.jakejmattson.discordkt.arguments.UserArg
import me.jakejmattson.discordkt.commands.CommandEvent
import me.jakejmattson.discordkt.commands.commands
import me.jakejmattson.discordkt.util.addField
import me.jakejmattson.discordkt.util.thumbnail
import me.jakejmattson.modmail.extensions.archiveString
import me.jakejmattson.modmail.services.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@Suppress("unused")
fun reportHelperCommands(configuration: Configuration, reportService: ReportService, loggingService: LoggingService) =
    commands("ReportHelpers") {

        suspend fun Member.openReport(event: CommandEvent<*>, detain: Boolean = false) {
            val guild = guild.asGuild()
            val reportCategory = configuration[guild]!!.getLiveReportCategory(guild.kord)

            getDmChannel().createEmbed {
                if (detain) {
                    color = Color.red.kColor
                    addField("You've have been detained by the staff of ${guild.name}!", Locale.USER_DETAIN_MESSAGE)
                } else {
                    color = Color.green.kColor
                    addField("Chatting with ${guild.name}!", Locale.BOT_DESCRIPTION)
                }

                thumbnail(guild.icon?.cdnUrl?.toUrl() ?: "")
            }

            val reportChannel = guild.createTextChannel(username) {
                parentId = reportCategory?.id
            }

            reportOpenEmbed(reportChannel, event.author, detain)

            val newReport = Report(id, reportChannel.id, guild.id, ConcurrentHashMap())
            reportService.addReport(newReport)

            if (detain) newReport.detain(guild.kord)

            loggingService.staffOpen(guild, reportChannel.name, event.author, detain)
            event.respond(reportChannel.mention)
        }

        user("Open a Report", "Open", Locale.OPEN_DESCRIPTION) {
            val targetMember = arg.asMemberOrNull(guild.id)

            if (targetMember == null) {
                println("User is no longer in this guild.")
                return@user
            }

            val openReport = targetMember.findReport()

            if (openReport != null) {
                respond("Open report: <#${openReport.channelId}>")
                return@user
            }

            try {
                targetMember.openReport(this, false)
            } catch (ex: RequestException) {
                respond("Unable to contact the target user. Direct messages are disabled or the bot is blocked.")
                return@user
            }
        }

        user("Detain this User", "Detain", Locale.DETAIN_DESCRIPTION) {
            val targetMember = arg.asMemberOrNull(guild.id)

            if (targetMember == null) {
                println("User is no longer in this guild.")
                return@user
            }

            if (targetMember.getPermissions().contains(discord.configuration.defaultPermissions)) {
                respond("You cannot detain another staff member.")
                return@user
            }

            if (targetMember.isDetained()) {
                respond("This member is already detained.")
                return@user
            }

            val openReport = targetMember.findReport()

            if (openReport != null) {
                openReport.detain(discord.kord)
                respond("Open report: <#${openReport.channelId}> (mute applied)")
                return@user
            }

            try {
                targetMember.openReport(this, true)
            } catch (ex: RequestException) {
                respond(
                    "Unable to contact the target user. " +
                            "Direct messages are disabled or the bot is blocked. " +
                            "Mute was not applied"
                )

                return@user
            }

            targetMember.mute()
        }

        slash("Release", Locale.RELEASE_DESCRIPTION) {
            execute {
                val report = channel.findReport()

                if (report == null) {
                    respond("This command must be run in a report channel")
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
                respondPublic("${member.tag} has been released.")
            }
        }

        slash("ID", Locale.ID_DESCRIPTION) {
            execute {
                val report = channel.findReport()

                if (report == null) {
                    respond("This command must be run in a report channel")
                    return@execute
                }

                respond(report.userId)
            }
        }

        slash("History", Locale.HISTORY_DESCRIPTION) {
            execute(UserArg) {
                val user = args.first
                val history = user.getDmChannel().archiveString().toByteArray()

                if (history.isNotEmpty())
                    interaction?.respondPublic {
                        addFile(
                            "$${user.id.value}.txt",
                            ChannelProvider { history.inputStream().toByteReadChannel() })
                    }
                else {
                    respond("No history available.")
                }
            }
        }
    }