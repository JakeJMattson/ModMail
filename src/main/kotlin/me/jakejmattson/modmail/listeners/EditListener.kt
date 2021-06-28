package me.jakejmattson.modmail.listeners

import dev.kord.core.behavior.edit
import dev.kord.core.event.channel.TypingStartEvent
import dev.kord.core.event.message.MessageDeleteEvent
import dev.kord.core.event.message.MessageUpdateEvent
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.modmail.extensions.cleanContent
import me.jakejmattson.modmail.services.*

@Suppress("unused")
fun editListener(discord: Discord, reportService: ReportService, loggingService: LoggingService) = listeners {
    on<MessageUpdateEvent> {
        val message = this.message.asMessage()
        val author = message.author!!

        if (getMessage().getGuildOrNull() != null) {
            if (author.id == kord.getSelf().id) return@on

            val report = channel.findReport() ?: return@on
            val privateChannel = kord.getUser(report.userId)?.getDmChannel() ?: return@on

            val targetMessage = report.messages[messageId]!!

            privateChannel.getMessage(targetMessage).edit {
                content = new.content.toString()
            }
        } else {
            val report = author.findReport() ?: return@on
            val liveReport = report.toLiveReport(kord) ?: return@on
            val targetMessage = report.messages[messageId] ?: return@on
            val channel = liveReport.channel
            val guildMessage = channel.getMessage(targetMessage)
            val newContent = message.cleanContent(discord)

            loggingService.edit(liveReport, guildMessage.cleanContent(discord), newContent)
            channel.getMessage(targetMessage).edit {
                content = newContent
            }
        }
    }

    on<MessageDeleteEvent> {
        getGuild() ?: return@on

        val report = channel.findReport() ?: return@on
        val targetMessage = report.messages[messageId] ?: return@on
        val privateChannel = kord.getUser(report.userId)?.getDmChannel() ?: return@on

        privateChannel.deleteMessage(targetMessage)
        report.messages.remove(messageId)

        reportService.writeReportToFile(report)
    }

    on<TypingStartEvent> {
        if (getGuild() != null)
            return@on

        user.toLiveReport()?.channel?.type()
    }
}