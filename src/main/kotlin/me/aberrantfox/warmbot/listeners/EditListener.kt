package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.message.guild.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageUpdateEvent
import net.dv8tion.jda.api.events.user.UserTypingEvent

class EditListener(private val reportService: ReportService, private val loggingService: LoggingService) {
    @Subscribe
    fun onGuildMessageUpdate(event: GuildMessageUpdateEvent) {
        val jda = event.jda

        if (event.author.id == jda.selfUser.id) return

        val report = event.channel.channelToReport() ?: return
        val privateChannel = jda.privateChannels.firstOrNull { it.user.id == report.userId } ?: return
        val targetMessage = report.messages[event.messageId]!!

        privateChannel.editMessageById(targetMessage, event.message).queue()
    }

    @Subscribe
    fun onGuildMessageDelete(event: GuildMessageDeleteEvent) {
        val report = event.channel.channelToReport() ?: return
        val targetMessage = report.messages[event.messageId] ?: return
        val privateChannel = event.jda.privateChannels.first { it.user.id == report.userId }

        privateChannel.deleteMessageById(targetMessage).queue()
        report.messages.remove(event.messageId)

        reportService.writeReportToFile(report)
    }

    @Subscribe
    fun onUserTypingEvent(event: UserTypingEvent) {
        event.privateChannel ?: return

        val report = event.user.userToReport() ?: return

        report.channelId.idToTextChannel()?.sendTyping()?.queue()
    }

    @Subscribe
    fun onPrivateMessageUpdate(event: PrivateMessageUpdateEvent) {
        val report = event.author.userToReport() ?: return
        val targetMessage = report.messages[event.messageId] ?: return
        val channel = report.channelId.idToTextChannel() ?: return
        val guildMessage = channel.retrieveMessageById(targetMessage).complete()

        loggingService.edit(report, guildMessage.cleanContent(), event.message.cleanContent())
        channel.editMessageById(targetMessage, event.message.cleanContent()).queue()
    }
}