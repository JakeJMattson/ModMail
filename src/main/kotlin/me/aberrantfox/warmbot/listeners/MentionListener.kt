package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.extensions.stdlib.trimToID
import me.aberrantfox.warmbot.extensions.selfUser
import me.aberrantfox.warmbot.services.InfoService
import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent

class MentionListener(private val infoService: InfoService) {
    @Subscribe
    fun onMessageReceived(event: GuildMessageReceivedEvent) {
        with (event) {
            if (author.isBot) return

            if (message.contentRaw.trimToID() == selfUser().id)
                channel.sendMessage(infoService.botInfo(channel)).queue()
        }
    }
}