package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.events.guild.GuildJoinEvent

class GuildJoinListener(private val conversationService: ConversationService,
                        val configuration: Configuration) {
    @Subscribe
    fun onGuildJoin(event: GuildJoinEvent) {
        if (configuration.guildConfigurations.any { g -> g.guildId == event.guild.id })
            return
        else
            conversationService.createConversation(event.guild.owner.user.id, event.guild.id, "guild-setup")
    }
}