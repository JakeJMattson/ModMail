package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class ResponseListener(private val reportService: ReportService, private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot)
            return

        val guildConfig = configuration.getGuildConfig(event.guild.id) ?: return

        if (event.message.contentRaw.startsWith(guildConfig.prefix))
            return

        if (reportService.isReportChannel(event.channel.id))
            reportService.sendToUser(event.channel.id, event.message)
    }
}