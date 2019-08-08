package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class ResponseListener(private val configuration: Configuration) {
    @Subscribe
    fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot) return

        if (!event.channel.isReportChannel()) return

        with(event.message) {
            if (contentRaw.startsWith(configuration.prefix)) return

            val report = event.channel.channelToReport()
            val member = report.reportToMember() ?: return addFailReaction()

            member.user.sendPrivateMessage(fullContent())
            report.queuedMessageId = id
        }
    }
}