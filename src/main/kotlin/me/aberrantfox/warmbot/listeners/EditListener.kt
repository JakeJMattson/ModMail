package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.message.guild.*
import net.dv8tion.jda.core.events.message.priv.*
import net.dv8tion.jda.core.events.user.UserTypingEvent

class EditListener(private val reportService: ReportService, private val loggingService: LoggingService) {
    @Subscribe
    fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        if (!reportService.isReportChannel(event.channel.id)) return

        if (event.author.id == selfUser().id) return

        val report = reportService.getReportByChannel(event.channel.id)
        val privateChannel = getPrivateChannels().first { it.user.id == report.userId }
        val targetMessage = report.messages[event.messageId]

        privateChannel.editMessageById(targetMessage, event.message).queue()
    }

    @Subscribe
    fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        if (!reportService.isReportChannel(event.channel.id)) return

        val report = reportService.getReportByChannel(event.channel.id)
        val targetMessage = report.messages[event.messageId] ?: return
        val privateChannel = getPrivateChannels().first { it.user.id == report.userId }

        privateChannel.deleteMessageById(targetMessage).queue()
        report.messages.remove(event.messageId)

        reportService.writeReportToFile(report)
    }

    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        if (!reportService.hasReportChannel(event.user.id)) return

        event.privateChannel ?: return

        val report = reportService.getReportByUserId(event.user.id)

        report.channelId.idToTextChannel().sendTyping().queue()
    }

    @Subscribe
    fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {
        if (!reportService.hasReportChannel(event.author.id)) return

        val report = reportService.getReportByUserId(event.author.id)
        val targetMessage = report.messages[event.messageId] ?: return
        val channel = report.channelId.idToTextChannel()
        val guildMessage = channel.getMessageById(targetMessage).complete()

        loggingService.edit(report, guildMessage.cleanContent(), event.message.cleanContent())
        channel.editMessageById(targetMessage, event.message).queue()
    }

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.id == selfUser().id) {
            if (event.message.embeds.isNotEmpty()) return

            val user = event.channel.id.idToPrivateChannel().user.id

            if (!reportService.hasReportChannel(user)) return

            val report = reportService.getReportByUserId(user)

            if (report.queuedMessageId != null) {
                report.messages[report.queuedMessageId!!] = event.messageId
                report.queuedMessageId = null

                reportService.writeReportToFile(report)
            }
        }
    }

    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.id == selfUser().id) {
            if (event.message.embeds.isNotEmpty()) return

            if (!reportService.isReportChannel(event.channel.id)) return

            val report = reportService.getReportByChannel(event.channel.id)

            if (report.queuedMessageId != null) {
                report.messages[report.queuedMessageId!!] = event.messageId
                report.queuedMessageId = null

                reportService.writeReportToFile(report)
            }
        }
    }
}