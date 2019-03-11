package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class ResponseListener(private val reportService: ReportService, private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        if (event.message.contentRaw.startsWith(configuration.prefix)) return

        if (!event.channel.isReportChannel()) return

        reportService.sendToUser(event.channel, event.message)
    }
}