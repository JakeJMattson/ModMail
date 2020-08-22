package me.jakejmattson.modmail.listeners

import com.google.common.eventbus.Subscribe
import me.jakejmattson.discordkt.api.services.ConversationService
import me.jakejmattson.modmail.conversations.GuildChoiceConversation
import me.jakejmattson.modmail.services.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent

class ReportListener(private val config: Configuration,
                     private val reportService: ReportService,
                     private val conversationService: ConversationService) {
    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val user = event.author.takeUnless { it.isBot } ?: return

        if (conversationService.hasConversation(user, user.openPrivateChannel().complete())) return

        val message = event.message
        val commonGuilds = user.mutualGuilds.filter { config.guildConfigurations[it.idLong] != null }

        when {
            user.findReport() != null -> {
                reportService.receiveFromUser(message)
            }
            commonGuilds.size > 1 -> {
                conversationService.startPrivateConversation<GuildChoiceConversation>(user, commonGuilds, message)
            }
            else -> {
                val guild = commonGuilds.firstOrNull() ?: return
                with(reportService) {
                    createReport(user, guild)
                    receiveFromUser(message)
                }
            }
        }
    }
}