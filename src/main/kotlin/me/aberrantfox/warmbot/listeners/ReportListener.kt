package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.isInteger
import me.aberrantfox.kjdautils.internal.command.ConversationService
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent

class ReportListener(private val reportService: ReportService, private val conversationService: ConversationService) {
    private val heldMessages = mutableMapOf<String, Message>()
    private val sentChoice = HashSet<String>()

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val user = event.author

        if (user.isBot || conversationService.hasConversation(user.id)) return

        val message = event.message
        val content = message.fullContent().trim()
        val commonGuilds = reportService.getCommonGuilds(user)

        when {
            reportService.hasReportChannel(user.id) -> {
                reportService.receiveFromUser(user, message)
            }
            sentChoice.contains(user.id) -> {
                if (!content.isInteger()) {
                    user.sendPrivateMessage("Choice must be an number.")
                    user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds))
                    return
                }

                val selection = content.toInt()

                if (selection !in 0..commonGuilds.lastIndex) {
                    user.sendPrivateMessage("Choice must be a valid guild ID.")
                    user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds))
                    return
                }

                val firstMessage = heldMessages.getOrDefault(user.id, message)
                reportService.addReport(user, commonGuilds[selection], firstMessage)
                reportService.receiveFromUser(user, firstMessage)
                user.sendPrivateMessage(reportService.buildReportOpenedEmbed(commonGuilds[selection]))
                heldMessages.remove(user.id)
                sentChoice.remove(user.id)
            }
            commonGuilds.size > 1 -> {
                heldMessages[user.id] = message
                user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds))
                sentChoice.add(user.id)
            }
            else -> {
                val guild = commonGuilds.first()
                reportService.apply {
                    addReport(user, guild, message)
                    receiveFromUser(user, message)
                }
                user.sendPrivateMessage(reportService.buildReportOpenedEmbed(guild))
            }
        }
    }
}