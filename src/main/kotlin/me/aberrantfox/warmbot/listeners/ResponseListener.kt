package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ResponseListener(val reportService: ReportService, val prefix: String) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if(event.author.isBot) {
            return
        }

        if(event.message.contentRaw.startsWith(prefix)) {
            return
        }

        val channel = event.channel

        if( !( reportService.isReportChannel(channel.id)) ) {
            return
        }

        reportService.sendToUser(channel.id, event.message.fullContent())
    }
}