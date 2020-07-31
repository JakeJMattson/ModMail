package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.AutoSetupConversation
import net.dv8tion.jda.api.entities.Guild

@Service
class GuildService(private val discord: Discord,
                   private val configuration: Configuration,
                   private val conversationService: ConversationService) {
    fun initDanglingGuilds() {
        discord.jda.guilds.filter { configuration[it.idLong] == null }.forEach { initInGuild(it) }
    }

    fun initInGuild(guild: Guild) {
        if (configuration[guild.idLong] == null) {
            startSetupConversation(guild)
        }
    }

    private fun startSetupConversation(guild: Guild) =
        conversationService.startPrivateConversation<AutoSetupConversation>(guild.owner!!.user, configuration, guild)
}