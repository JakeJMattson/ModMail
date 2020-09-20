package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import kotlinx.coroutines.flow.toList
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.discordkt.api.extensions.mutualGuilds
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.GuildChoiceConversation
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.services.*

fun reportListener(config: Configuration, reportService: ReportService, conversationService: ConversationService) = listeners {
    on<MessageCreateEvent> {
        val user = message.author!!.takeUnless { it.isBot == true } ?: return@on

        if (getGuild() == null) {
            if (conversationService.hasConversation(user, message.channel)) return@on

            val validGuilds = user.mutualGuilds.toList().filter { config.guildConfigurations[it.id.longValue] != null }

            when {
                user.findReport() != null -> reportService.receiveFromUser(message)
                validGuilds.size > 1 -> {
                    println("Starting convo")
                    conversationService.startPrivateConversation<GuildChoiceConversation>(user, validGuilds, message)
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
                val liveReport = report.toLiveReport(kord) ?: return@on addFailReaction()
                val content = fullContent().takeUnless { it.trim().isBlank() } ?: return@on

                println(report)
                println(content)
                println(liveReport.user.getDmChannel())

                val newMessage = liveReport.user.getDmChannel().createMessage(content)

                report.messages[id.value] = newMessage.id.value
            }
        }
    }
}