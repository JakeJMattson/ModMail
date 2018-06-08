package me.aberrantfox.warmbot.listeners

import com.google.common.eventbus.Subscribe
import me.aberrantfox.kjdautils.api.dsl.embed
import me.aberrantfox.kjdautils.extensions.jda.sendPrivateMessage
import me.aberrantfox.warmbot.extensions.fullContent
import me.aberrantfox.warmbot.services.ReportService
import net.dv8tion.jda.core.entities.Guild
import net.dv8tion.jda.core.entities.User
import net.dv8tion.jda.core.events.message.priv.PrivateMessageReceivedEvent
import java.awt.Color

class ReportListener(val reportService: ReportService) {

    @Subscribe
    fun onPrivateMessageReceived(event: PrivateMessageReceivedEvent) {

        if (event.author.isBot) {
            return
        }

        val user = event.author
        val message = event.message.fullContent()

        if (reportService.hasReportChannel(user.id)) {
            reportService.receiveFromUser(user, message)
        } else if (user.mutualGuilds.size > 1 && hasNumericArgument(message)) {
            if (guildIndexValid(user, getNumericArgument(message))) {
                reportService.addReport(user, user.mutualGuilds[getNumericArgument(message)], true)
                reportService.receiveFromUser(user, message)
                sendReportOpenedEmbed(user, user.mutualGuilds[getNumericArgument(message)])
            } else {
                user.sendPrivateMessage(
                        "**I'm sorry, that guild selection is not valid. Please choose another.**")
                sendGuildChoiceEmbed(user)
            }
        } else if (user.mutualGuilds.size > 1) {
            sendGuildChoiceEmbed(user)
        } else {
            reportService.addReport(user, user.mutualGuilds.first(), false)
            reportService.receiveFromUser(user, message)
        }
    }

    private fun sendGuildChoiceEmbed(userObject: User) {
        userObject.sendPrivateMessage(embed {
            setColor(Color.CYAN)
            setAuthor("Please choose which server's staff you'd like to contact.")
            setThumbnail(userObject.jda.selfUser.avatarUrl)
            description("Respond with the number that correlates with the desired server to get started.")
            addBlankField(true)

            userObject.mutualGuilds.forEachIndexed { index, guild ->
                field {
                    name = "$index) ${guild.name}"
                    inline = false
                }
            }
        })
    }

    private fun sendReportOpenedEmbed(userObject: User, guildObject: Guild) {
        userObject.sendPrivateMessage(embed {
            setColor(Color.PINK)
            setAuthor("You've successfully opened a report with the staff of ${guildObject.name}")
            description("Someone will respond shortly, please be patient.")
            setThumbnail(guildObject.iconUrl)
        })
    }
}

private fun guildIndexValid(userObject: User, index: Int) = index in (0..userObject.mutualGuilds.size)

//TODO: Rewrite/replace this with syntactic sugar of some kind ::  (Only supports 0-9 as valid guild choices.)

private fun getNumericArgument(message: String): Int {
    return message.toCharArray()[0].toString().toInt()
}

private fun hasNumericArgument(message: String): Boolean {
    return message.toCharArray()[0].isDigit()
}
