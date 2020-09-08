package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.common.entity.Snowflake
import com.gitlab.kordlib.core.behavior.edit
import com.gitlab.kordlib.core.event.channel.TypingStartEvent
import com.gitlab.kordlib.core.event.message.*
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.toSnowflake
import me.jakejmattson.modmail.extensions.cleanContent
import me.jakejmattson.modmail.services.*

fun editListener(discord: Discord, reportService: ReportService, loggingService: LoggingService) = listeners {
    on<MessageUpdateEvent> {
        val message = this.message.asMessage()
        val author = message.author!!

        if (getMessage().getGuildOrNull() != null) {
            if (author.id == kord.getSelf().id) return@on

            val report = channel.findReport() ?: return@on
            val privateChannel = report.userId.toSnowflake()?.let { kord.getUser(it)?.getDmChannel() } ?: return@on
            val targetMessage = Snowflake(report.messages[messageId.value]!!)

            privateChannel.getMessage(targetMessage).edit {
                content = new.content
            }
        }
        else {
            val report = author.findReport() ?: return@on
            val liveReport = report.toLiveReport(kord) ?: return@on
            val targetMessage = report.messages[messageId.value]?.toSnowflake() ?: return@on
            val channel = liveReport.channel
            val guildMessage = channel.getMessage(targetMessage)
            val newContent = message.cleanContent(discord)

            loggingService.edit(liveReport, guildMessage.cleanContent(discord), newContent)
            channel.getMessage(targetMessage).edit {
                content = newContent
            }
        }
    }

    on<TypingStartEvent> {
        if (getGuild() != null)
            return@on

        user.toLiveReport()?.channel?.type()
    }

    on<MessageDeleteEvent> {
        getGuild() ?: return@on

        val report = channel.findReport() ?: return@on
        val targetMessage = report.messages[messageId.value]?.toSnowflake() ?: return@on
        val privateChannel = report.userId.toSnowflake()?.let { kord.getUser(it)?.getDmChannel() } ?: return@on

        privateChannel.deleteMessage(targetMessage)
        report.messages.remove(messageId.value)

        reportService.writeReportToFile(report)
    }
}