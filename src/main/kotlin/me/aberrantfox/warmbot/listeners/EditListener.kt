package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.message.guild.*
import net.dv8tion.jda.api.events.message.priv.*
import net.dv8tion.jda.api.events.user.UserTypingEvent

class EditListener(private val reportService: ReportService, private val loggingService: LoggingService) {
    @Subscribe
    fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        if (!event.channel.isReportChannel()) return

        if (event.author.id == selfUser().id) return

        val report = event.channel.channelToReport()
        val privateChannel = getPrivateChannels().first { it.user.id == report.userId }
        val targetMessage = report.messages[event.messageId]!!

        privateChannel.editMessageById(targetMessage, event.message).queue()
    }

    @Subscribe
    fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        if (!event.channel.isReportChannel()) return

        val report = event.channel.channelToReport()
        val targetMessage = report.messages[event.messageId] ?: return
        val privateChannel = getPrivateChannels().first { it.user.id == report.userId }

        privateChannel.deleteMessageById(targetMessage).queue()
        report.messages.remove(event.messageId)

        reportService.writeReportToFile(report)
    }

    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        if (!event.user.hasReportChannel()) return

        event.privateChannel ?: return

        val report = event.user.userToReport() ?: return

        report.channelId.idToTextChannel()?.sendTyping()?.queue()
    }

    @Subscribe
    fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {
        if (!event.author.hasReportChannel()) return

        val report = event.author.userToReport() ?: return
        val targetMessage = report.messages[event.messageId] ?: return
        val channel = report.channelId.idToTextChannel() ?: return
        val guildMessage = channel.retrieveMessageById(targetMessage).complete()

        loggingService.edit(report, guildMessage.cleanContent(), event.message.cleanContent())
        channel.editMessageById(targetMessage, event.message.cleanContent()).queue()
    }

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        if (event.author.id == selfUser().id) {
            if (event.message.embeds.isNotEmpty()) return

            val user = event.channel.id.idToPrivateChannel()?.user ?: return

            if (!user.hasReportChannel()) return

            val report = user.userToReport() ?: return

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

            if (!event.channel.isReportChannel()) return

            val report = event.channel.channelToReport()

            if (report.queuedMessageId != null) {
                report.messages[report.queuedMessageId!!] = event.messageId
                report.queuedMessageId = null

                reportService.writeReportToFile(report)
            }
        }
    }
}