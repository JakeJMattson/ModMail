package me.jakejmattson.modmail.services

import com.gitlab.kordlib.core.entity.Guild
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.modmail.conversations.guildSetupConversation

@Service
class GuildService(private val discord: Discord, private val configuration: Configuration) {
    suspend fun initInGuild(guild: Guild) {
        //configuration[guild.id.longValue] ?: startSetupConversation(guild)
    }

    private suspend fun startSetupConversation(guild: Guild)
        = guildSetupConversation(discord, configuration, guild).startPrivately(guild.owner.asUser())
}