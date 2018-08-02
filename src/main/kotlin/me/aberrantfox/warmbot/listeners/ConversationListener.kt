package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.services.ConversationService
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent

class ConversationListener(private val conversationService: ConversationService,
                           private val reportService: ReportService) {
    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot)
            return

        if (conversationService.hasConversation(event.author.id))
            conversationService.handleResponse(event.author.id, event)

    }
}