package me.jakejmattson.modmail.listeners

import com.google.common.eventbus.Subscribe
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.events.channel.text.TextChannelDeleteEvent

val deletionQueue = ArrayList<String>()

class ChannelDeletionListener(private val loggingService: LoggingService) {
    @Subscribe
    fun onTextChannelDelete(event: TextChannelDeleteEvent) {
        val channel = event.channel
        val report = channel.findReport() ?: return

        report.close(channel.jda)

        if (channel.id in deletionQueue) {
            deletionQueue.remove(channel.id)
            return
        }

        loggingService.manualClose(event.guild, channel.name)
    }
}