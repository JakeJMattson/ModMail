package me.aberrantfox.warmbot.conversations

import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.conversation.*
import me.jakejmattson.kutils.api.services.PersistenceService
import net.dv8tion.jda.api.entities.Guild

class GuildSetupConversation(private val persistenceService: PersistenceService) : Conversation() {
    @Start
    fun guildSetupConversation(config: Configuration, guild: Guild) = conversation {
        respond("Starting manual setup.")

        val reportCategory = blockingPromptUntil(
            argumentType = CategoryArg,
            initialPrompt = { "Enter the **Category ID** of the category where new reports will be created." },
            until = { it.guild == guild },
            errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "report category") }
        )

        val archiveChannel = blockingPromptUntil(
            argumentType = TextChannelArg,
            initialPrompt = { "Enter the **Channel ID** of the channel where archived reports will be sent." },
            until = { it.guild == guild },
            errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "archive channel") }
        )

        val loggingChannel = blockingPromptUntil(
            argumentType = TextChannelArg,
            initialPrompt = { "Enter the **Channel ID** of the channel where information will be logged." },
            until = { it.guild == guild },
            errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "logging channel") }
        )

        val staffRole = blockingPromptUntil(
            argumentType = RoleArg(guild.id),
            initialPrompt = { "Enter the **Role Name** of the role required to give commands to this bot." },
            until = { it.guild == guild },
            errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "staff role") }
        )

        val logConfig = LoggingConfiguration(loggingChannel.id)
        val guildConfig = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, staffRole.name, logConfig)

        config.guildConfigurations.add(guildConfig)
        persistenceService.save(config)

        respond(Locale.GUILD_SETUP_SUCCESSFUL)
    }
}
