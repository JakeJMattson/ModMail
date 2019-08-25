package me.aberrantfox.warmbot.services

import me.aberrantfox.kjdautils.api.annotation.Service
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.warmbot.extensions.*
import net.dv8tion.jda.api.entities.Guild
import java.util.Timer
import kotlin.concurrent.schedule

@Service
class GuildService(private val configuration: Configuration, private val conversationService: ConversationService,
                   private val persistenceService: PersistenceService, jdaInitializer: JdaInitializer) {
    init { consolidateWhitelist() }

    fun cleanseGuilds() =
        guilds().forEach {
            if (it.id !in configuration.whitelist) {
                it.leave().queue()
                configuration.guildConfigurations.remove(configuration.getGuildConfig(it.id))
                persistenceService.save(configuration)
            }
        }

    fun initOrLeave(guild: Guild) {
        if (!configuration.hasGuildConfig(guild.id)) {
            Timer().schedule(15000) {
                if (guild.id in configuration.whitelist) startSetupConversation(guild) else guild.leave().queue()
            }
        } else if (guild.id !in configuration.whitelist) {
            configuration.whitelist.add(guild.id)
        }
    }

    private fun startSetupConversation(guild: Guild) =
        conversationService.createConversation(guild.ownerId, guild.id, "auto-setup")

    private fun consolidateWhitelist() = configuration.apply {
        guildConfigurations.forEach { if (it.guildId !in whitelist) whitelist.add(it.guildId) }
        guilds().filter { !hasGuildConfig(it.id) }.forEach { initOrLeave(it) }
    }
}