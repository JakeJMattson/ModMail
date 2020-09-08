package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.entity.Guild
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.GuildSetupConversation

@Service
class GuildService(private val configuration: Configuration, private val conversationService: ConversationService) {
    suspend fun initInGuild(guild: Guild) {
        //configuration[guild.id.longValue] ?: startSetupConversation(guild)
    }

    private suspend fun startSetupConversation(guild: Guild) =
        conversationService.startPrivateConversation<GuildSetupConversation>(guild.owner.asUser(), configuration, guild)
}