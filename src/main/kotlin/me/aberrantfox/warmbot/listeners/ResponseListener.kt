package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.GuildConfiguration
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.core.hooks.ListenerAdapter

class ResponseListener(private val reportService: ReportService, private val guildConfigurations: List<GuildConfiguration>) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) {
            return
        }

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