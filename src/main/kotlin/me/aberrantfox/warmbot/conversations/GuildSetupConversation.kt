package me.aberrantfox.warmbot.conversations

import me.aberrantfox.kjdautils.api.dsl.*
import me.aberrantfox.kjdautils.internal.arguments.*
import me.aberrantfox.kjdautils.internal.di.PersistenceService
import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*

@Convo
fun guildSetupConversation(config: Configuration, persistenceService: PersistenceService) = conversation("guild-setup") {
    respond("Starting manual setup. If you make a mistake, you can adjust the provided values using commands later.")

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

    val commandChannel = blockingPromptUntil(
        argumentType = TextChannelArg,
        initialPrompt = { "Enter the **Channel ID** where commands can be used. More can be added later." },
        until = { it.guild == guild },
        errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "command channel") }
    )

    val staffRole = blockingPromptUntil(
        argumentType = RoleArg(guild.id),
        initialPrompt = { "Enter the **Role Name** of the role required to give commands to this bot." },
        until = { it.guild == guild },
        errorMessage = { inject({ FAIL_GUILD_SETUP }, "field" to "staff role") }
    )

    val staffChannels = arrayListOf(commandChannel.id)
    val logConfig = LoggingConfiguration(loggingChannel.id)
    val guildConfig = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, staffRole.name, staffChannels, logConfig)

    config.guildConfigurations.add(guildConfig)
    persistenceService.save(config)

    respond(Locale.GUILD_SETUP_SUCCESSFUL)
}
