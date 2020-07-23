package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.*
import me.jakejmattson.kutils.api.dsl.embed.embed
import me.jakejmattson.kutils.api.extensions.jda.sendPrivateMessage
import me.jakejmattson.kutils.api.extensions.stdlib.isInteger
import me.jakejmattson.kutils.api.services.ConversationService
import net.dv8tion.jda.api.entities.*
import net.dv8tion.jda.api.events.message.priv.PrivateMessageReceivedEvent

class ReportListener(private val reportService: ReportService, private val conversationService: ConversationService) {
    private val heldMessages = mutableMapOf<String, Message>()
    private val sentChoice = HashSet<String>()

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {
        val user = event.author

        if (user.isBot || conversationService.hasConversation(user, user.openPrivateChannel().complete())) return

        val message = event.message
        val content = message.fullContent().trim()
        val commonGuilds = reportService.getCommonGuilds(user)

        when {
            user.findReport() != null -> {
                reportService.receiveFromUser(message)
            }
            sentChoice.contains(user.id) -> {
                if (!content.isInteger())
                    return user.sendPrivateMessage("Choice must be an number.")

                val selection = content.toInt()

                if (selection !in 0..commonGuilds.lastIndex)
                    return user.sendPrivateMessage("Choice must be a valid guild ID.")

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
            thumbnail = commonGuilds.first().jda.selfUser.effectiveAvatarUrl
            color = infoColor

            commonGuilds.forEachIndexed { index, guild ->
                field {
                    name = "$index) ${guild.name}"
                    inline = false
                }
            }
        }
}