package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.mutualGuilds
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.GuildChoiceConversation
import me.jakejmattson.modmail.services.*

fun reportListener(config: Configuration, reportService: ReportService, conversationService: ConversationService) = listeners {
    on<MessageCreateEvent> {
        if (getGuild() != null) return@on

        val user = message.author!!.takeUnless { it.isBot == true } ?: return@on

        if (conversationService.hasConversation(user, message.channel)) return@on

        val validGuilds = user.mutualGuilds.toList().filter { config.guildConfigurations[it.id.longValue] != null }

        when {
            user.findReport() != null -> {
                println("Report found")
                reportService.receiveFromUser(message)
            }
            validGuilds.size > 1 -> {
                println("Starting convo")
                conversationService.startPrivateConversation<GuildChoiceConversation>(user, validGuilds, message)
            }
            else -> {
                println("Creating report")

                val guild = validGuilds.firstOrNull() ?: return@on
                with(reportService) {
                    createReport(user, guild)
                    receiveFromUser(message)
                }
            }
        }
    }
}