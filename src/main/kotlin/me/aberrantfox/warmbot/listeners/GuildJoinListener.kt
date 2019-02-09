package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.warmbot.services.Configuration
import net.dv8tion.jda.core.events.guild.GuildJoinEvent
import java.util.Timer
import kotlin.concurrent.schedule

class GuildJoinListener(val configuration: Configuration, private val conversationService: ConversationService) {
    @Subscribe
    fun onGuildJoin(event: GuildJoinEvent) {
        val guild = event.guild

        if (!configuration.hasGuildConfig(guild.id)) {
            Timer().schedule(15000) {
                if (guild.id in configuration.whitelist) {
                    conversationService.createConversation(guild.ownerId, guild.id, "guild-setup")
                } else {
                    guild.leave().queue()
                }
            }
        }
    }
}