package me.aberrantfox.warmbot.conversations

import me.aberrantfox.warmbot.messages.*
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.conversation.*
import net.dv8tion.jda.api.entities.Guild

class GuildSetupConversation : Conversation() {
    @Start
    fun guildSetupConversation(config: Configuration, guild: Guild) = conversation {
        respond("Starting manual setup.")

        val reportCategory = promptUntil(
            argumentType = CategoryArg,
            prompt = "Enter the **Category ID** of the category where new reports will be created.",
            error = Locale.FAIL_GUILD_SETUP inject ("field" to "report category"),
            isValid = { it.guild == guild }
        )

        val archiveChannel = promptUntil(
            argumentType = TextChannelArg,
            prompt = "Enter the **Channel ID** of the channel where archived reports will be sent.",
            error = Locale.FAIL_GUILD_SETUP inject ("field" to "archive channel"),
            isValid = { it.guild == guild }
        )

        val loggingChannel = promptUntil(
            argumentType = TextChannelArg,
            prompt = "Enter the **Channel ID** of the channel where information will be logged.",
            error = Locale.FAIL_GUILD_SETUP inject ("field" to "logging channel"),
            isValid = { it.guild == guild }
        )

        val staffRole = promptUntil(
            argumentType = RoleArg(guild.id),
            prompt = "Enter the **Role Name** of the role required to give commands to this bot.",
            error = Locale.FAIL_GUILD_SETUP inject ("field" to "staff role"),
            isValid = { it.guild == guild }
        )

        val logConfig = LoggingConfiguration(loggingChannel.id)
        val guildConfig = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, staffRole.name, logConfig)

        config.guildConfigurations.add(guildConfig)
        config.save()

        respond(Locale.GUILD_SETUP_SUCCESSFUL)
    }
}
