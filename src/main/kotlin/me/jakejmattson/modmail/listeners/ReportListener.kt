package me.jakejmattson.modmail.listeners

import dev.kord.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.Discord
import me.jakejmattson.discordkt.conversations.Conversations
import me.jakejmattson.discordkt.dsl.listeners
import me.jakejmattson.discordkt.extensions.mutualGuilds
import me.jakejmattson.discordkt.extensions.sendPrivateMessage
import me.jakejmattson.modmail.conversations.guildChoiceConversation
import me.jakejmattson.modmail.extensions.addFailReaction
import me.jakejmattson.modmail.extensions.fullContent
import me.jakejmattson.modmail.services.Configuration
import me.jakejmattson.modmail.services.ReportService
import me.jakejmattson.modmail.services.findReport
import kotlin.collections.set

@Suppress("unused")
fun reportListener(discord: Discord, config: Configuration, reportService: ReportService) = listeners {
    on<MessageCreateEvent> {
        val user = message.author!!.takeUnless { it.isBot } ?: return@on

        if (getGuild() == null) {
            if (Conversations.hasConversation(user, message.channel.asChannel())) return@on

            val validGuilds = user.mutualGuilds.toList().filter { config.guildConfigurations[it.id] != null }

            when {
                user.findReport() != null -> reportService.receiveFromUser(message)
                validGuilds.size > 1 -> {
                    guildChoiceConversation(discord, message).startPrivately(discord, user)
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
                val live = report.toLiveReport(kord) ?: return@on addFailReaction()
                val prefix = config[getGuild()]?.prefix ?: return@on
                val content = fullContent().takeUnless { it.isBlank() || it.startsWith(prefix) } ?: return@on
                val newMessage = live.user.sendPrivateMessage(content)

                report.messages[id] = newMessage.id
            }
        }
    }
}