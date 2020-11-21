package me.jakejmattson.modmail.conversations

import com.gitlab.kordlib.core.entity.Guild
import com.gitlab.kordlib.core.entity.channel.TextChannel
import com.gitlab.kordlib.rest.Image
import me.jakejmattson.discordkt.api.arguments.*
import me.jakejmattson.discordkt.api.dsl.conversation
import me.jakejmattson.modmail.messages.Locale
import me.jakejmattson.modmail.services.*
import kotlin.collections.set

fun guildSetupConversation(config: Configuration, guild: Guild) = conversation {
    respond {
        title = guild.name
        description = "Starting setup..."
        thumbnail {
            url = guild.getIconUrl(Image.Format.PNG).toString()
        }
    }

    suspend fun <T> createPrompt(arg: ArgumentType<T>, prompt: String, isValid: (T) -> Boolean) = promptUntil(arg, prompt, "Input must be from the original guild.", isValid)

    val reportCategory = createPrompt(CategoryArg(guildId = guild.id), "Enter the category where new reports are created.") { it.guild == guild }
    val archiveChannel = createPrompt(ChannelArg<TextChannel>(), "Enter the channel where archived reports will be sent.") { it.guild == guild }
    val loggingChannel = createPrompt(ChannelArg<TextChannel>(), "Enter the channel where information will be logged.") { it.guild == guild }
    val staffRole = createPrompt(RoleArg(guildId = guild.id), "Enter the role required to give commands to this bot.") { it.guild == guild }
    val guildConfig = GuildConfiguration("!", reportCategory.id.value, archiveChannel.id.value, staffRole.id.value, LoggingConfiguration(loggingChannel.id.value))

    config.guildConfigurations[guild.id.value] = guildConfig
    config.save()

    respond(Locale.GUILD_SETUP_SUCCESSFUL)
}
