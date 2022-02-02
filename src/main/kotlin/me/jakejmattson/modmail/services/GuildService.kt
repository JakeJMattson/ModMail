package me.jakejmattson.modmail.services

import dev.kord.core.entity.Guild
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.annotations.Service
import me.jakejmattson.modmail.conversations.guildSetupConversation

@Service
class GuildService(private val discord: Discord, private val configuration: Configuration) {
    suspend fun initInGuild(guild: Guild) {
        //configuration[guild.id.value] ?: startSetupConversation(guild)
    }

    private suspend fun startSetupConversation(guild: Guild) = guildSetupConversation(configuration, guild).startPrivately(discord, guild.owner.asUser())
}