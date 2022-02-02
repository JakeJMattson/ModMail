package me.jakejmattson.modmail.conversations

import dev.kord.core.entity.Guild
import dev.kord.core.entity.Message
import dev.kord.x.emoji.DiscordEmoji
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.conversations.conversation
import me.jakejmattson.discordkt.api.extensions.mutualGuilds
import me.jakejmattson.discordkt.api.extensions.pfpUrl
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.ReportService

fun guildChoiceConversation(discord: Discord, message: Message) = conversation {

    val reportService = discord.getInjectionObjects<ReportService>()
    val config = discord.getInjectionObjects<Configuration>()
    val guilds = user.mutualGuilds.toList().filter { config.guildConfigurations[it.id] != null }

    val guild = promptButton<Guild> {
        embed {
            title = "Select Server"
            description = "Select the server you want to contact."
            thumbnail {
                url = discord.kord.getSelf().pfpUrl
            }
        }

        guilds.toList().chunked(5).forEach { row ->
            buttons {
                row.forEach { guild ->
                    button(guild.name, null, guild)
                }
            }
        }
    }

    with(reportService) {
        createReport(user, guild)
        receiveFromUser(message)
    }
}