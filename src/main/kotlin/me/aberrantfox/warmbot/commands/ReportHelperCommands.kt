package me.aberrantfox.warmbot.commands

import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.annotations.CommandSet
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.command.*
import me.jakejmattson.kutils.api.dsl.embed.EmbedDSLHandle.Companion.failureColor
import me.jakejmattson.kutils.api.dsl.embed.EmbedDSLHandle.Companion.successColor
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.extensions.jda.*
import net.dv8tion.jda.api.entities.*
import java.awt.Color
import java.util.concurrent.ConcurrentHashMap

@CommandSet("ReportHelpers")
fun reportHelperCommands(configuration: Configuration, reportService: ReportService,
                         moderationService: ModerationService, loggingService: LoggingService) = commands {

    data class EmbedData(val color: Color, val topic: String, val openMessage: String, val initialMessage: String)

    fun openReport(event: CommandEvent<*>, targetUser: User, guild: Guild, userEmbed: MessageEmbed, embedData: EmbedData, detain: Boolean = false) {
        val guildId = guild.id
        val reportCategory = configuration.getGuildConfig(guildId)!!.getLiveReportCategory(guild.jda)

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
                        color = embedData.color
                        thumbnail = targetUser.effectiveAvatarUrl
                        addField(embedData.topic,
                            "${targetUser.descriptor()} :: ${targetUser.asMention}",
                            false)
                        addField(embedData.openMessage,
                            "${event.author.descriptor()} :: ${event.author.asMention}",
                            false)
                        addField("Initial Message", initialMessage, false)
                    }

                    channel.sendMessage(reportEmbed).queue()

                    val newReport = Report(targetUser.id, channel.id, guildId, ConcurrentHashMap())
                    reportService.addReport(newReport)

                    if (detain) newReport.detain()

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
                addField("You've received a message from the staff of ${guild.name}!", Locale.BOT_DESCRIPTION, false)
            }

            val embedData = EmbedData(successColor, "New Report Opened!", "This report was opened by", message)
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

            val embedData = EmbedData(failureColor, "User Detained!", "This user was detained by", message)
            openReport(event, targetMember.user, guild, userEmbed, embedData, true)
        }
    }

    command("Release") {
        description = Locale.RELEASE_DESCRIPTION
        execute(MemberArg) {
            val targetMember = it.args.first

            if (!targetMember.isDetained())
                return@execute it.respond("This member is not detained.")

            targetMember.user.findReport()?.release()
            it.respond("${targetMember.fullName()} has been released.")
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