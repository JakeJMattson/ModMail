package me.aberrantfox.warmbot.services

import me.aberrantfox.warmbot.conversations.AutoSetupConversation
import me.jakejmattson.kutils.api.Discord
import me.jakejmattson.kutils.api.annotations.Service
import me.jakejmattson.kutils.api.services.ConversationService
import net.dv8tion.jda.api.entities.Guild

@Service
class GuildService(private val discord: Discord,
                   private val configuration: Configuration,
                   private val conversationService: ConversationService) {
    fun initDanglingGuilds() {
        discord.jda.guilds.filter { !configuration.hasGuildConfig(it.id) }.forEach { initInGuild(it) }
    }

    fun initInGuild(guild: Guild) {
        if (!configuration.hasGuildConfig(guild.id)) {
            startSetupConversation(guild)
        }
    }

    private fun startSetupConversation(guild: Guild) =
        conversationService.startPrivateConversation<AutoSetupConversation>(guild.owner!!.user, configuration, guild)
}