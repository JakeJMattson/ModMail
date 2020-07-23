package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class ResponseListener(private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        with(event.message) {
            if (contentRaw.startsWith(configuration.prefix)) return

            val report = event.channel.channelToReport() ?: return
            val member = report.reportToMember() ?: return addFailReaction()
            val content = fullContent().takeUnless { it.trim().isBlank() } ?: return

            member.user.openPrivateChannel().queue {
                it.sendMessage(content).queue { receivedMessage ->
                    report.messages[id] = receivedMessage.id
                }
            }
        }
    }
}