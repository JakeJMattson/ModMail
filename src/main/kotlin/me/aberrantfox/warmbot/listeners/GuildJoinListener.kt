package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.core.events.guild.GuildJoinEvent

class GuildJoinListener(val configuration: Configuration, private val guildService: GuildService) {
    @Subscribe fun onGuildJoin(event: GuildJoinEvent) = guildService.initOrLeave(event.guild)
}