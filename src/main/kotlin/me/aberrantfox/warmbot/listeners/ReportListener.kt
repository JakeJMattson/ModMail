package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.Configuration
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.Message
import net.dv8tion.jda.core.entities.MessageEmbed
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import java.awt.Color

class ReportListener(private val reportService: ReportService) {

    private val heldMessages = mutableMapOf<String, String>()

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot) {
            return
        }

        val user = event.author
        val message = event.message.fullContent().trim()
        val commonGuilds = reportService.getCommonGuilds(event.author)

        if (reportService.hasReportChannel(user.id)) {
            reportService.receiveFromUser(user, message)
        } else if (commonGuilds.size > 1 && isNumericArgument(message)) {
            if (isGuildSelectionValid(commonGuilds, message.toInt())) {
                reportService.addReport(user, commonGuilds[message.toInt()])
                reportService.receiveFromUser(user,
                        heldMessages.getOrDefault(user.id,
                                "**Error :: Could not retrieve initial message from user.**"))
                user.sendPrivateMessage(reportService.buildReportOpenedEmbed(commonGuilds[message.toInt()]))
                heldMessages.remove(user.id)
            } else {
                user.sendPrivateMessage(
                        "**I'm sorry, that guild selection is not valid. Please choose another.**")
                user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds))
            }
        } else if (commonGuilds.size > 1) {
            heldMessages[user.id] = message
            user.sendPrivateMessage(reportService.buildGuildChoiceEmbed(commonGuilds))
        } else {
            reportService.addReport(user, commonGuilds.first())
            reportService.receiveFromUser(user, message)
            user.sendPrivateMessage(reportService.buildReportOpenedEmbed(commonGuilds.first()))
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
