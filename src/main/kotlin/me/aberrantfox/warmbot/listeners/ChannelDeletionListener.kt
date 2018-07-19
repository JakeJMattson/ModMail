package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent

class ChannelDeletionListener(val reportService: ReportService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        if (reportService.isReportChannel(event.channel.id)) {
            reportService.removeReport(event.channel.id)
        }
    }
}