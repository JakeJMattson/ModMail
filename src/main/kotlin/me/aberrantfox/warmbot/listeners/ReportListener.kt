package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.kjdautils.internal.logging.DefaultLogger
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.*

import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent

class ReportListener(private val reportService: ReportService, private val conversationService: ConversationService) {

    private val heldMessages = mutableMapOf<String, Message>()

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot)
            return

        if (conversationService.hasConversation(event.author.id))
            return

        val user = event.author
        val message = event.message
        val content = message.fullContent().trim()
        val commonGuilds = event.author.mutualGuilds

        if (reportService.hasReportChannel(user.id)) {
            reportService.receiveFromUser(user, message)
        } else if (commonGuilds.size > 1 && isNumericArgument(content)) {
            if (isGuildSelectionValid(commonGuilds, content.toInt())) {
                val firstMessage = heldMessages.get(user.id)!!
                reportService.addReport(user, commonGuilds[content.toInt()], firstMessage)
                reportService.receiveFromUser(user, firstMessage)
                user.sendPrivateMessage(reportService.buildReportOpenedEmbed(commonGuilds[content.toInt()]), DefaultLogger())
                heldMessages.remove(user.id)
            } else {
                user.sendPrivateMessage(
                        "**I'm sorry, that guild selection is not valid. Please choose another.**", DefaultLogger())
                user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds), DefaultLogger())
            }
        } else if (commonGuilds.size > 1) {
            heldMessages[user.id] = message
            user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds), DefaultLogger())
        } else {
            reportService.addReport(user, commonGuilds.first(), message)
            reportService.receiveFromUser(user, message)
            user.sendPrivateMessage(reportService.buildReportOpenedEmbed(commonGuilds.first()), DefaultLogger())
        }
    }

    private fun isGuildSelectionValid(commonGuilds: List<Guild>, index: Int) = index in (0..(commonGuilds.size - 1))

    private fun isNumericArgument(message: String): Boolean {
        return try {
            message.toInt()
            true
        } catch (e: Exception) {
            false
        }
    }
}
