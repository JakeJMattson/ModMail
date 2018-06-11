package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ChannelDeletionListener(val reportService: ReportService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        if (reportService.isReportChannel(event.channel.id)) {
            reportService.sendReportClosedEmbed(reportService.getReportByChannel(event.channel.id))
            reportService.removeReport(event.channel.id)
        }
    }
}