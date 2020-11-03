package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.Discord
import me.jakejmattson.discordkt.api.dsl.*
import me.jakejmattson.discordkt.api.extensions.*
import me.jakejmattson.modmail.conversations.guildChoiceConversation
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.services.*

fun reportListener(discord: Discord, config: Configuration, reportService: ReportService) = listeners {
    on<MessageCreateEvent> {
        val user = message.author!!.takeUnless { it.isBot == true } ?: return@on

        if (getGuild() == null) {
            if (Conversations.hasConversation(user, message.channel)) return@on

            val validGuilds = user.mutualGuilds.toList().filter { config.guildConfigurations[it.id.longValue] != null }

            when {
                user.findReport() != null -> reportService.receiveFromUser(message)
                validGuilds.size > 1 -> {
                    guildChoiceConversation(reportService, validGuilds, message).startPrivately(discord, user)
                }
                else -> {
                    val guild = validGuilds.firstOrNull() ?: return@on
                    with(reportService) {
                        createReport(user, guild)
                        receiveFromUser(message)
                    }
                }
            }
        } else {
            with(message) {
                val report = channel.findReport() ?: return@on
                report.toLiveReport(kord) ?: return@on addFailReaction()
                val prefix = config[getGuild().id.longValue]?.prefix ?: return@on
                val content = fullContent().takeUnless { it.isBlank() || it.startsWith(prefix) } ?: return@on
                val newMessage = user.sendPrivateMessage(content)

                report.messages[id.value] = newMessage.id.value
            }
        }
    }
}