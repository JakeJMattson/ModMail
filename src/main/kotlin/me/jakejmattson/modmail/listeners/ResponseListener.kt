package me.jakejmattson.modmail.listeners

import com.google.common.eventbus.Subscribe
import me.jakejmattson.modmail.extensions.*
import me.jakejmattson.modmail.services.findReport
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class ResponseListener {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        with(event.message) {
            //TODO Figure out how to check for the relevant prefix

            val report = event.channel.findReport() ?: return
            val liveReport = report.toLiveReport(jda) ?: return addFailReaction()
            val content = fullContent().takeUnless { it.trim().isBlank() } ?: return

            liveReport.user.openPrivateChannel().queue {
                it.sendMessage(content).queue { receivedMessage ->
                    report.messages[id] = receivedMessage.id
                }
            }
        }
    }
}