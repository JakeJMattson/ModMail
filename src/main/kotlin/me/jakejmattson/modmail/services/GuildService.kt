package me.jakejmattson.modmail.services

import me.jakejmattson.discordkt.api.annotations.Service
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.AutoSetupConversation
import net.dv8tion.jda.api.entities.Guild

@Service
class GuildService(private val configuration: Configuration, private val conversationService: ConversationService) {
    fun initInGuild(guild: Guild) {
        if (configuration[guild.idLong] == null) {
            startSetupConversation(guild)
        }
    }

    private fun startSetupConversation(guild: Guild) =
        conversationService.startPrivateConversation<AutoSetupConversation>(guild.retrieveOwner().complete().user, configuration, guild)
}