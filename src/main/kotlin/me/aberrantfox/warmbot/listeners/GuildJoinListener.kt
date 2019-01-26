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
        if (!configuration.hasGuildConfig(event.guild.id)) {
            Timer().schedule(15000) {
                if (event.guild.id in configuration.whitelist) {
                    conversationService.createConversation(event.guild.owner.user.id, event.guild.id, "guild-setup")
                } else {
                    event.guild.leave().queue()
                }
            }
        }
    }
}