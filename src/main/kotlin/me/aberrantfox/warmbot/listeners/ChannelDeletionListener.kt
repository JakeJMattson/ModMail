package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent

class ChannelDeletionListener(private val reportService: ReportService, private val auditLogPollingService: AuditLogPollingService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        val channel = event.channel

        if (channel.isReportChannel()) {
            auditLogPollingService.registerChannel(channel)
            reportService.closeReport(channel.channelToReport())
        }
    }
}