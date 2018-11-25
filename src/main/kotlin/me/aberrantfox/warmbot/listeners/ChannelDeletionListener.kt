package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent

class ChannelDeletionListener(private val reportService: ReportService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        if (reportService.isReportChannel(event.channel.id)) {
            val report = reportService.getReportByChannel(event.channel.id)
            reportService.closeReport(report)
        }
    }
}