package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent

val deletionQueue = ArrayList<String>()

class ChannelDeletionListener(private val loggingService: LoggingService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        val channel = event.channel
        val report = channel.channelToReport() ?: return

        report.close()

        if (channel.id in deletionQueue) {
            deletionQueue.remove(channel.id)
            return
        }

        loggingService.manualClose(event.guild, channel.name)
    }
}