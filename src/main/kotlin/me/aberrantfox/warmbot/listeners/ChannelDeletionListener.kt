package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.channel.text.TextChannelDeleteEvent

class ChannelDeletionListener(private val service: ReportService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) =
        if (event.channel.isReportChannel()) event.channel.channelToReport().let { service.closeReport(it) } else Unit
}