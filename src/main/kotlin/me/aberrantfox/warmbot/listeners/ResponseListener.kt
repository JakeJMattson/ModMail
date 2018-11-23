package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent

class ResponseListener(private val reportService: ReportService, private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }

        val guildConfigurations = configuration.guildConfigurations

        if(!guildConfigurations.any { g -> g.guildId == event.guild.id})
            return

        if (event.message.contentRaw.startsWith(guildConfigurations.first { g -> g.guildId == event.guild.id}.prefix)) {
            return
        }

        val channel = event.channel

        if (!(reportService.isReportChannel(channel.id))) {
            return
        }

        reportService.sendToUser(channel.id, event.message)
    }
}