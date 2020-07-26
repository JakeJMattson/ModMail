package me.aberrantfox.warmbot.conversations

import me.aberrantfox.warmbot.messages.Locale
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.arguments.*
import me.jakejmattson.kutils.api.dsl.arguments.ArgumentType
import me.jakejmattson.kutils.api.dsl.conversation.*
import net.dv8tion.jda.api.entities.Guild

class GuildSetupConversation : Conversation() {
    @Start
    fun guildSetupConversation(config: Configuration, guild: Guild) = conversation {
        respond("Starting manual setup.")

        fun <T> createPrompt(arg: ArgumentType<T>, prompt: String, isValid: (T) -> Boolean) = promptUntil(arg, prompt, "Input must be from the original guild.", isValid)

        val reportCategory = createPrompt(CategoryArg(guildId = guild.id), "Enter the category where new reports are created.") { it.guild == guild }
        val archiveChannel = createPrompt(TextChannelArg, "Enter the channel where archived reports will be sent.") { it.guild == guild }
        val loggingChannel = createPrompt(TextChannelArg, "Enter the channel where information will be logged.") { it.guild == guild }
        val staffRole = createPrompt(RoleArg(guildId = guild.id), "Enter the role required to give commands to this bot.") { it.guild == guild }
        val guildConfig = GuildConfiguration(guild.id, reportCategory.id, archiveChannel.id, staffRole.name, LoggingConfiguration(loggingChannel.id))

        config.guildConfigurations.add(guildConfig)
        config.save()

        respond(Locale.GUILD_SETUP_SUCCESSFUL)
    }
}
