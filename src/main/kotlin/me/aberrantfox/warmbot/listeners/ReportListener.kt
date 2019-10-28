package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.kjdautils.extensions.stdlib.isInteger
import me.aberrantfox.kjdautils.internal.services.ConversationService
import me.aberrantfox.warmbot.extensions.*
import me.aberrantfox.warmbot.services.*
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent
import java.awt.Color

class ReportListener(private val reportService: ReportService, private val conversationService: ConversationService) {
    private val heldMessages = mutableMapOf<String, Message>()
    private val sentChoice = HashSet<String>()

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val user = event.author

        if (user.isBot || conversationService.hasConversation(user)) return

        val message = event.message
        val content = message.fullContent().trim()
        val commonGuilds = reportService.getCommonGuilds(user)

        when {
            user.hasReportChannel() -> {
                reportService.receiveFromUser(message)
            }
            sentChoice.contains(user.id) -> {
                if (!content.isInteger()) {
                    user.sendPrivateMessage("Choice must be an number.")
                    user.sendPrivateMessage(buildGuildChoiceEmbed(commonGuilds))
                    return
                }

                val selection = content.toInt()

                if (selection !in 0..commonGuilds.lastIndex) {
                    user.sendPrivateMessage("Choice must be a valid guild ID.")
                    user.sendPrivateMessage(buildGuildChoiceEmbed(commonGuilds))
                    return
                }

                val firstMessage = heldMessages.getOrDefault(user.id, message)
                reportService.createReport(user, commonGuilds[selection], firstMessage)
                reportService.receiveFromUser(firstMessage)
                heldMessages.remove(user.id)
                sentChoice.remove(user.id)
            }
            commonGuilds.size > 1 -> {
                heldMessages[user.id] = message
                user.sendPrivateMessage(buildGuildChoiceEmbed(commonGuilds))
                sentChoice.add(user.id)
            }
            else -> {
                val guild = commonGuilds.first()
                reportService.apply {
                    createReport(user, guild, message)
                    receiveFromUser(message)
                }
            }
        }
    }

    private fun buildGuildChoiceEmbed(commonGuilds: List<Guild>) =
        embed {
            title = "Please choose which server's staff you'd like to contact."
            description = "Respond with the number that correlates with the desired server to get started."
            thumbnail = selfUser().effectiveAvatarUrl
            color = Color.CYAN

            commonGuilds.forEachIndexed { index, guild ->
                field {
                    name = "$index) ${guild.name}"
                    inline = false
                }
            }
        }
}