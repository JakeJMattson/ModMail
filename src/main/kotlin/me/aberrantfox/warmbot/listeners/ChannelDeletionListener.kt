package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent

class ChannelDeletionListener(private val service: ReportService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) =
        if (service.isReportChannel(event.channel.id)) service.getReportByChannel(event.channel.id).let { service.closeReport(it) } else Unit
}