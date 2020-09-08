package me.jakejmattson.modmail.listeners

import com.gitlab.kordlib.core.event.message.MessageCreateEvent
import me.jakejmattson.discordkt.api.dsl.listeners
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.services.findReport

fun responseListener() = listeners {
    on<MessageCreateEvent> {
        getGuild() ?: return@on
        if (message.author?.isBot == true) return@on

        with(message) {
            val report = channel.findReport() ?: return@on
            val liveReport = report.toLiveReport(kord) ?: return@on addFailReaction()
            val content = fullContent().takeUnless { it.trim().isBlank() } ?: return@on
            val newMessage = liveReport.user.getDmChannel().createMessage(content)

            report.messages[id.value] = newMessage.id.value
        }
    }
}